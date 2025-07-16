package com.nathaniel.bookbackend.events;

import java.time.Instant;

public class BookUpdateEvent {
    private String bookId;
    private String updateType;
    private String userId;
    private Instant timestamp;

    // Constructors
    public BookUpdateEvent() {}

    public BookUpdateEvent(String bookId, String updateType, String userId) {
        this.bookId = bookId;
        this.updateType = updateType;
        this.userId = userId;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
