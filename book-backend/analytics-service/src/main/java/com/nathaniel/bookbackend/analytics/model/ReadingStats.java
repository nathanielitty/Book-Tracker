package com.nathaniel.bookbackend.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reading_stats")
public class ReadingStats {
    @Id
    private String userId;

    private Integer totalBooksRead;
    private Integer totalBooksInProgress;
    private Integer totalPagesRead;
    private Double averageRating;
    private Integer totalReviews;
    private Double averageReadingTime; // in days
    private Integer currentReadingStreak;
    private Integer longestReadingStreak;
    
    @Column(columnDefinition = "jsonb")
    private String favoriteGenres; // JSON array of genre frequencies

    private Instant lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Integer getTotalBooksRead() { return totalBooksRead; }
    public void setTotalBooksRead(Integer totalBooksRead) { this.totalBooksRead = totalBooksRead; }

    public Integer getTotalBooksInProgress() { return totalBooksInProgress; }
    public void setTotalBooksInProgress(Integer totalBooksInProgress) { 
        this.totalBooksInProgress = totalBooksInProgress; 
    }

    public Integer getTotalPagesRead() { return totalPagesRead; }
    public void setTotalPagesRead(Integer totalPagesRead) { this.totalPagesRead = totalPagesRead; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Double getAverageReadingTime() { return averageReadingTime; }
    public void setAverageReadingTime(Double averageReadingTime) { 
        this.averageReadingTime = averageReadingTime; 
    }

    public Integer getCurrentReadingStreak() { return currentReadingStreak; }
    public void setCurrentReadingStreak(Integer currentReadingStreak) { 
        this.currentReadingStreak = currentReadingStreak; 
    }

    public Integer getLongestReadingStreak() { return longestReadingStreak; }
    public void setLongestReadingStreak(Integer longestReadingStreak) { 
        this.longestReadingStreak = longestReadingStreak; 
    }

    public String getFavoriteGenres() { return favoriteGenres; }
    public void setFavoriteGenres(String favoriteGenres) { this.favoriteGenres = favoriteGenres; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
