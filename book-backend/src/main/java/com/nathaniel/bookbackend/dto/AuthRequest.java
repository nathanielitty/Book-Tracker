package com.nathaniel.bookbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Data Transfer Object for authentication requests.
 * Supports both login (username/email + password) and registration (username + email + password).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password") // Never log passwords
public class AuthRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    @JsonProperty("username")
    private String username;
    
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required", groups = RegistrationValidation.class)
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @JsonProperty("email")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one lowercase letter, one uppercase letter, one digit, and one special character"
    )
    @JsonProperty("password")
    private String password;
    
    /**
     * Validation group for registration-specific validations
     */
    public interface RegistrationValidation {}
    
    /**
     * Validation group for login-specific validations  
     */
    public interface LoginValidation {}
}