package com.nathaniel.bookbackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super("AUTHENTICATION_FAILED", message, "Invalid credentials. Please check your username and password.", HttpStatus.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super("AUTHENTICATION_FAILED", message, "Invalid credentials. Please check your username and password.", HttpStatus.UNAUTHORIZED, cause);
    }
    
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password");
    }
    
    public static AuthenticationException userNotFound(String identifier) {
        return new AuthenticationException(String.format("User not found: %s", identifier));
    }
    
    public static AuthenticationException accountLocked(String username) {
        return new AuthenticationException(String.format("Account locked: %s", username));
    }
}
