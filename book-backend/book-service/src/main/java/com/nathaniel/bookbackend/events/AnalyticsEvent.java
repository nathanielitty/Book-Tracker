package com.nathaniel.bookbackend.events;

import java.time.Instant;
import java.util.Map;

public class AnalyticsEvent {
    private String eventType;
    private String userId;
    private String resourceId;
    private Map<String, String> metadata;
    private Instant timestamp;

    // Constructors
    public AnalyticsEvent() {}

    public AnalyticsEvent(String eventType, String userId, String resourceId, Map<String, String> metadata) {
        this.eventType = eventType;
        this.userId = userId;
        this.resourceId = resourceId;
        this.metadata = metadata;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
