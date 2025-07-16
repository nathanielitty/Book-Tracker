package com.nathaniel.bookbackend.events;

import java.time.Instant;

public class NotificationEvent {
    private String userId;
    private String type;
    private String message;
    private Instant timestamp;

    // Constructors
    public NotificationEvent() {}

    public NotificationEvent(String userId, String type, String message) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
