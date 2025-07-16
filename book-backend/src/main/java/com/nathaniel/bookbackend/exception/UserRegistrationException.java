package com.nathaniel.bookbackend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user registration fails due to business rules
 */
public class UserRegistrationException extends BusinessException {
    
    public UserRegistrationException(String message) {
        super("USER_REGISTRATION_FAILED", message, "Registration failed. Please check your information and try again.", HttpStatus.BAD_REQUEST);
    }
    
    public UserRegistrationException(String message, Throwable cause) {
        super("USER_REGISTRATION_FAILED", message, "Registration failed. Please check your information and try again.", HttpStatus.BAD_REQUEST, cause);
    }
    
    public static UserRegistrationException usernameAlreadyExists(String username) {
        return new UserRegistrationException(String.format("Username '%s' is already taken", username));
    }
    
    public static UserRegistrationException emailAlreadyExists(String email) {
        return new UserRegistrationException(String.format("Email '%s' is already registered", email));
    }
}
