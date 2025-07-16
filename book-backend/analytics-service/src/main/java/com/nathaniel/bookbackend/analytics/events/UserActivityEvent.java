package com.nathaniel.bookbackend.analytics.events;

import java.time.Instant;
import java.util.Map;

public class UserActivityEvent {
    private String userId;
    private String activityType;
    private String bookId;
    private Map<String, String> metadata;
    private Instant timestamp;

    public UserActivityEvent() {}

    public UserActivityEvent(String userId, String activityType, String bookId, Map<String, String> metadata) {
        this.userId = userId;
        this.activityType = activityType;
        this.bookId = bookId;
        this.metadata = metadata;
        this.timestamp = Instant.now();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
