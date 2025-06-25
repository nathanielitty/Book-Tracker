package com.nathaniel.bookbackend.auth.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityEvent {
    private String userId;
    private String action;
    private String details;
    private long timestamp;

    public UserActivityEvent(String userId, String action) {
        this.userId = userId;
        this.action = action;
        this.timestamp = System.currentTimeMillis();
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
