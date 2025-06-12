package com.nathaniel.bookbackend.service.impl;

import com.nathaniel.bookbackend.dto.AuthRequest;
import com.nathaniel.bookbackend.dto.AuthResponse;
import com.nathaniel.bookbackend.exception.AuthenticationException;
import com.nathaniel.bookbackend.exception.UserRegistrationException;
import com.nathaniel.bookbackend.models.AppUser;
import com.nathaniel.bookbackend.repository.AppUserRepository;
import com.nathaniel.bookbackend.security.JwtUtil;
import com.nathaniel.bookbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Implementation of AuthService following Netflix standards
 * Includes comprehensive logging, error handling, and transaction management
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        log.info("Attempting to register new user with username: {}", request.getUsername());
        
        // Validate business rules
        validateRegistrationRequest(request);
        
        try {
            // Create new user
            AppUser user = AppUser.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            user = userRepository.save(user);
            log.info("Successfully registered user with ID: {} and username: {}", user.getId(), user.getUsername());
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());
            Instant expiresAt = Instant.now().plus(jwtUtil.getExpirationTimeMs(), ChronoUnit.MILLIS);
            
            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTimeMs() / 1000) // Convert to seconds
                    .expiresAt(expiresAt)
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .createdAt(user.getCreatedAt())
                            .build())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to register user with username: {}", request.getUsername(), e);
            throw new UserRegistrationException("Registration failed due to internal error", e);
        }
    }
    
    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Attempting to authenticate user with username: {}", request.getUsername());
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Find user details
            AppUser user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> AuthenticationException.userNotFound(userDetails.getUsername()));
            
            log.info("Successfully authenticated user with ID: {} and username: {}", user.getId(), user.getUsername());
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            Instant expiresAt = Instant.now().plus(jwtUtil.getExpirationTimeMs(), ChronoUnit.MILLIS);
            
            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTimeMs() / 1000) // Convert to seconds
                    .expiresAt(expiresAt)
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .createdAt(user.getCreatedAt())
                            .build())
                    .build();
                    
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for username: {} - Invalid credentials", request.getUsername());
            throw AuthenticationException.invalidCredentials();
        } catch (Exception e) {
            log.error("Authentication failed for username: {} due to unexpected error", request.getUsername(), e);
            throw new AuthenticationException("Authentication failed due to internal error", e);
        }
    }
    
    @Override
    public boolean isUsernameAvailable(String username) {
        boolean available = !userRepository.existsByUsername(username);
        log.debug("Username '{}' availability check: {}", username, available ? "available" : "taken");
        return available;
    }
    
    @Override
    public boolean isEmailAvailable(String email) {
        boolean available = !userRepository.existsByEmail(email);
        log.debug("Email '{}' availability check: {}", email, available ? "available" : "taken");
        return available;
    }
    
    private void validateRegistrationRequest(AuthRequest request) {
        log.debug("Validating registration request for username: {}", request.getUsername());
        
        if (!isUsernameAvailable(request.getUsername())) {
            log.warn("Registration failed - Username already exists: {}", request.getUsername());
            throw UserRegistrationException.usernameAlreadyExists(request.getUsername());
        }
        
        if (!isEmailAvailable(request.getEmail())) {
            log.warn("Registration failed - Email already exists: {}", request.getEmail());
            throw UserRegistrationException.emailAlreadyExists(request.getEmail());
        }
        
        log.debug("Registration request validation passed for username: {}", request.getUsername());
    }
}
