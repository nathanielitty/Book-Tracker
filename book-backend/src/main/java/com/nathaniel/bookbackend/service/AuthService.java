package com.nathaniel.bookbackend.service;

import com.nathaniel.bookbackend.dto.AuthRequest;
import com.nathaniel.bookbackend.dto.AuthResponse;
import com.nathaniel.bookbackend.exception.UserRegistrationException;
import com.nathaniel.bookbackend.exception.AuthenticationException;

/**
 * Service interface for user authentication operations
 * Follows Netflix service design patterns with clear contracts
 */
public interface AuthService {
    
    /**
     * Registers a new user in the system
     * 
     * @param request The registration request containing user details
     * @return AuthResponse with user information and access token
     * @throws UserRegistrationException if registration fails due to business rules
     */
    AuthResponse register(AuthRequest request);
    
    /**
     * Authenticates a user with username/email and password
     * 
     * @param request The login request containing credentials
     * @return AuthResponse with user information and access token
     * @throws AuthenticationException if authentication fails
     */
    AuthResponse login(AuthRequest request);
    
    /**
     * Validates if a username is available for registration
     * 
     * @param username The username to check
     * @return true if available, false if taken
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * Validates if an email is available for registration
     * 
     * @param email The email to check
     * @return true if available, false if taken
     */
    boolean isEmailAvailable(String email);
}
