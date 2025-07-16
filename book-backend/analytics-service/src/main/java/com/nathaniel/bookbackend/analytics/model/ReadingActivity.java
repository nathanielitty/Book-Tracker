package com.nathaniel.bookbackend.analytics.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reading_activities")
public class ReadingActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;
    private String bookId;
    private String activityType;
    private Integer pagesRead;
    private Double timeSpent; // in minutes
    private Instant activityDate;
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (activityDate == null) {
            activityDate = createdAt;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public Integer getPagesRead() { return pagesRead; }
    public void setPagesRead(Integer pagesRead) { this.pagesRead = pagesRead; }

    public Double getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Double timeSpent) { this.timeSpent = timeSpent; }

    public Instant getActivityDate() { return activityDate; }
    public void setActivityDate(Instant activityDate) { this.activityDate = activityDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
