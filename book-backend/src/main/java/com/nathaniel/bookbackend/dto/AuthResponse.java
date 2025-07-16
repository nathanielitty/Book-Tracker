package com.nathaniel.bookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for successful authentication
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private Long expiresIn; // seconds until expiration
    
    @JsonProperty("expires_at")
    private Instant expiresAt;
    
    @JsonProperty("user")
    private UserInfo user;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        @JsonProperty("id")
        private UUID id;
        
        @JsonProperty("username")
        private String username;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("created_at")
        private Instant createdAt;
    }
}