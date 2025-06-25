package com.nathaniel.bookbackend.notification.events;

import java.time.Instant;

public class UserActivityEvent {
    private String userId;
    private String activityType;
    private String bookId;
    private Instant timestamp;

    // Constructors
    public UserActivityEvent() {}

    public UserActivityEvent(String userId, String activityType, String bookId) {
        this.userId = userId;
        this.activityType = activityType;
        this.bookId = bookId;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
