package com.nathaniel.bookbackend.analytics.controller;

import com.nathaniel.bookbackend.analytics.model.ReadingActivity;
import com.nathaniel.bookbackend.analytics.model.ReadingStats;
import com.nathaniel.bookbackend.analytics.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<ReadingStats> getUserStats(@PathVariable String userId) {
        return ResponseEntity.ok(analyticsService.getUserStats(userId));
    }

    @GetMapping("/users/{userId}/activities")
    public ResponseEntity<Page<ReadingActivity>> getUserActivities(
            @PathVariable String userId,
            Pageable pageable) {
        return ResponseEntity.ok(analyticsService.getUserActivities(userId, pageable));
    }

    @GetMapping("/users/{userId}/progress")
    public ResponseEntity<Map<String, Object>> getReadingProgress(
            @PathVariable String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return ResponseEntity.ok(analyticsService.getReadingProgress(userId, start, end));
    }

    @PostMapping("/users/{userId}/refresh-stats")
    public ResponseEntity<Void> refreshUserStats(@PathVariable String userId) {
        analyticsService.updateUserStats(userId);
        return ResponseEntity.ok().build();
    }
}
