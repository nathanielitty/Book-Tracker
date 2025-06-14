package com.nathaniel.bookbackend.controllers;

import com.nathaniel.bookbackend.dto.ApiResponse;
import com.nathaniel.bookbackend.dto.AuthRequest;
import com.nathaniel.bookbackend.dto.AuthResponse;
import com.nathaniel.bookbackend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication controller following Netflix REST API standards
 * Provides comprehensive authentication endpoints with proper documentation
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user
     * Creates a new user account with username, email, and password
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody AuthRequest request) {
        
        log.info("Registration request received for username: {}", request.getUsername());
        
        AuthResponse response = authService.register(request);
        
        log.info("User registered successfully with ID: {}", response.getUser().getId());
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }
    
    /**
     * Authenticate user
     * Authenticates user with username/email and password, returns JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody AuthRequest request) {
        
        log.info("Login request received for username: {}", request.getUsername());
        
        AuthResponse response = authService.login(request);
        
        log.info("User authenticated successfully: {}", request.getUsername());
        
        return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
    }
    
    /**
     * Check username availability
     * Checks if a username is available for registration
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUsernameAvailability(
            @RequestParam String username) {
        
        log.debug("Checking username availability: {}", username);
        
        boolean available = authService.isUsernameAvailable(username);
        Map<String, Boolean> result = Map.of("available", available);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * Check email availability
     * Checks if an email is available for registration
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEmailAvailability(
            @RequestParam String email) {
        
        log.debug("Checking email availability: {}", email);
        
        boolean available = authService.isEmailAvailable(email);
        Map<String, Boolean> result = Map.of("available", available);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * Health check endpoint for authentication service
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "auth-service",
            "timestamp", java.time.Instant.now().toString()
        );
        return ResponseEntity.ok(ApiResponse.success(health, "Service is healthy"));
    }
    
    /**
     * Availability check endpoint for authentication service
     */
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> availability() {
        Map<String, Boolean> availability = Map.of("available", true);
        return ResponseEntity.ok(ApiResponse.success(availability, "Service is available"));
    }
}