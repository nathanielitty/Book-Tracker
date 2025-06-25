package com.nathaniel.bookbackend.analytics.service;

import com.nathaniel.bookbackend.analytics.model.ReadingActivity;
import com.nathaniel.bookbackend.analytics.model.ReadingStats;
import com.nathaniel.bookbackend.analytics.repository.ReadingActivityRepository;
import com.nathaniel.bookbackend.analytics.repository.ReadingStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nathaniel.bookbackend.analytics.events.UserActivityEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {
    
    @Autowired
    private ReadingActivityRepository activityRepository;
    
    @Autowired
    private ReadingStatsRepository statsRepository;
    
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "user-activity", groupId = "analytics-service")
    public void handleUserActivity(UserActivityEvent event) {
        ReadingActivity activity = new ReadingActivity();
        activity.setUserId(event.getUserId());
        activity.setBookId(event.getBookId());
        activity.setActivityType(event.getActivityType());
        
        if (event.getActivityType().equals("PROGRESS_UPDATED")) {
            // Assuming the event contains pages read in metadata
            Map<String, String> metadata = event.getMetadata();
            if (metadata != null && metadata.containsKey("pagesRead")) {
                activity.setPagesRead(Integer.parseInt(metadata.get("pagesRead")));
            }
        }
        
        activityRepository.save(activity);
        updateUserStats(event.getUserId());
    }

    @Transactional
    public void updateUserStats(String userId) {
        ReadingStats stats = statsRepository.findById(userId)
                .orElse(new ReadingStats());
        
        stats.setUserId(userId);
        
        // Calculate total pages read in last 30 days
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        Integer recentPagesRead = activityRepository.getTotalPagesReadBetween(
            userId, thirtyDaysAgo, Instant.now());
        stats.setTotalPagesRead(recentPagesRead != null ? recentPagesRead : 0);
        
        // Calculate reading streak
        calculateReadingStreak(stats, userId);
        
        // Update favorite genres
        updateFavoriteGenres(stats, userId);
        
        statsRepository.save(stats);
    }

    private void calculateReadingStreak(ReadingStats stats, String userId) {
        Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant thirtyDaysAgo = today.minus(30, ChronoUnit.DAYS);
        
        List<ReadingActivity> recentActivities = activityRepository
                .findByUserIdAndActivityDateBetween(userId, thirtyDaysAgo, today);
        
        int currentStreak = 0;
        int longestStreak = 0;
        int tempStreak = 0;
        Instant lastDate = null;
        
        for (ReadingActivity activity : recentActivities) {
            Instant activityDate = activity.getActivityDate().truncatedTo(ChronoUnit.DAYS);
            
            if (lastDate == null || 
                activityDate.equals(lastDate.plus(1, ChronoUnit.DAYS))) {
                tempStreak++;
            } else {
                if (tempStreak > longestStreak) {
                    longestStreak = tempStreak;
                }
                tempStreak = 1;
            }
            
            if (activityDate.equals(today)) {
                currentStreak = tempStreak;
            }
            
            lastDate = activityDate;
        }
        
        stats.setCurrentReadingStreak(currentStreak);
        stats.setLongestReadingStreak(Math.max(longestStreak, currentStreak));
    }

    private void updateFavoriteGenres(ReadingStats stats, String userId) {
        // This would typically involve fetching book genres from the book service
        // and calculating frequencies
        Map<String, Integer> genreFrequencies = new HashMap<>();
        // ... calculation logic ...
        try {
            String genresJson = objectMapper.writeValueAsString(genreFrequencies);
            stats.setFavoriteGenres(genresJson);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize favorite genres", e);
        }
    }

    @Transactional(readOnly = true)
    public ReadingStats getUserStats(String userId) {
        return statsRepository.findById(userId)
                .orElseGet(() -> {
                    ReadingStats stats = new ReadingStats();
                    stats.setUserId(userId);
                    return stats;
                });
    }

    @Transactional(readOnly = true)
    public Page<ReadingActivity> getUserActivities(String userId, Pageable pageable) {
        return activityRepository.findByUserIdOrderByActivityDateDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReadingProgress(String userId, Instant start, Instant end) {
        Integer pagesRead = activityRepository.getTotalPagesReadBetween(userId, start, end);
        Double timeSpent = activityRepository.getTotalTimeSpentBetween(userId, start, end);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("pagesRead", pagesRead != null ? pagesRead : 0);
        progress.put("timeSpent", timeSpent != null ? timeSpent : 0.0);
        progress.put("startDate", start);
        progress.put("endDate", end);
        
        return progress;
    }
}
