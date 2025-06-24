package com.nathaniel.bookbackend.controllers;

import com.nathaniel.bookbackend.config.TestSecurityConfig;
import com.nathaniel.bookbackend.dto.AuthRequest;
import com.nathaniel.bookbackend.dto.AuthResponse;
import com.nathaniel.bookbackend.exception.AuthenticationException;
import com.nathaniel.bookbackend.exception.UserRegistrationException;
import com.nathaniel.bookbackend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController following Netflix standards
 * Uses MockMvc for web layer testing with comprehensive coverage
 */
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
@DisplayName("AuthController Unit Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest validAuthRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        validAuthRequest = AuthRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        authResponse = AuthResponse.builder()
                .accessToken("jwt_token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .expiresAt(Instant.now().plusSeconds(3600))
                .user(AuthResponse.UserInfo.builder()
                        .id(UUID.randomUUID())
                        .username("testuser")
                        .email("test@example.com")
                        .createdAt(Instant.now())
                        .build())
                .build();
    }

    @Nested
    @DisplayName("Health Check Endpoints")
    class HealthCheckTests {

        @Test
        @DisplayName("Should return UP status for health endpoint")
        void shouldReturnHealthStatus() throws Exception {
            mockMvc.perform(get("/api/v1/auth/health"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Service is healthy"))
                    .andExpect(jsonPath("$.data.status").value("UP"));
        }

        @Test
        @DisplayName("Should return service availability status")
        void shouldReturnAvailabilityStatus() throws Exception {
            mockMvc.perform(get("/api/v1/auth/availability"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Service is available"))
                    .andExpect(jsonPath("$.data.available").value(true));
        }
    }

    @Nested
    @DisplayName("User Registration")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register user with valid data")
        void shouldRegisterUserSuccessfully() throws Exception {
            // Given
            when(authService.register(any(AuthRequest.class))).thenReturn(authResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validAuthRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.access_token").value("jwt_token"))
                    .andExpect(jsonPath("$.data.token_type").value("Bearer"))
                    .andExpect(jsonPath("$.data.user.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return 400 for invalid registration data")
        void shouldReturn400ForInvalidData() throws Exception {
            // Given
            AuthRequest invalidRequest = AuthRequest.builder()
                    .username("") // Invalid: empty username
                    .email("invalid-email") // Invalid: malformed email
                    .password("123") // Invalid: too short
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.errors").exists());
        }

        @Test
        @DisplayName("Should return 409 when username already exists")
        void shouldReturn409WhenUsernameExists() throws Exception {
            // Given
            when(authService.register(any(AuthRequest.class)))
                    .thenThrow(new UserRegistrationException("Username already exists"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validAuthRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Username already exists"));
        }
    }

    @Nested
    @DisplayName("User Login")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login user with valid credentials")
        void shouldLoginUserSuccessfully() throws Exception {
            // Given
            when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validAuthRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.access_token").value("jwt_token"))
                    .andExpect(jsonPath("$.data.token_type").value("Bearer"))
                    .andExpect(jsonPath("$.data.user.username").value("testuser"));
        }

        @Test
        @DisplayName("Should return 401 for invalid credentials")
        void shouldReturn401ForInvalidCredentials() throws Exception {
            // Given
            when(authService.login(any(AuthRequest.class)))
                    .thenThrow(new AuthenticationException("Invalid username or password"));

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validAuthRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        @Test
        @DisplayName("Should return 400 for missing login data")
        void shouldReturn400ForMissingData() throws Exception {
            // Given
            AuthRequest invalidRequest = AuthRequest.builder()
                    .username("") // Missing username
                    .password("") // Missing password
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.errors").exists());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate username availability")
        void shouldValidateUsernameAvailability() throws Exception {
            // Given
            when(authService.isUsernameAvailable("newuser")).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/validate/username")
                            .param("username", "newuser"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(true));
        }

        @Test
        @DisplayName("Should validate email availability")
        void shouldValidateEmailAvailability() throws Exception {
            // Given
            when(authService.isEmailAvailable("new@example.com")).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/auth/validate/email")
                            .param("email", "new@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.available").value(true));
        }
    }
}
