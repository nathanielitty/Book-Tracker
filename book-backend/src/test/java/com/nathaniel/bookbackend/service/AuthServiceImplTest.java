package com.nathaniel.bookbackend.service;

import com.nathaniel.bookbackend.dto.AuthRequest;
import com.nathaniel.bookbackend.dto.AuthResponse;
import com.nathaniel.bookbackend.exception.AuthenticationException;
import com.nathaniel.bookbackend.exception.UserRegistrationException;
import com.nathaniel.bookbackend.models.AppUser;
import com.nathaniel.bookbackend.repository.AppUserRepository;
import com.nathaniel.bookbackend.security.JwtUtil;
import com.nathaniel.bookbackend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl following Netflix Java standards.
 * Uses AssertJ for fluent assertions and comprehensive test coverage.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceImplTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthRequest validAuthRequest;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        validAuthRequest = AuthRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        testUser = AppUser.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("User Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register new user with valid data")
        void shouldRegisterNewUserSuccessfully() {
            // Given
            when(userRepository.existsByUsername(validAuthRequest.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(validAuthRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(validAuthRequest.getPassword())).thenReturn("encoded_password");
            when(userRepository.save(any(AppUser.class))).thenReturn(testUser);
            when(jwtUtil.generateToken(testUser.getUsername())).thenReturn("jwt_token");
            when(jwtUtil.getExpirationTimeMs()).thenReturn(3600000L);

            // When
            AuthResponse result = authService.register(validAuthRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("jwt_token");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            assertThat(result.getExpiresIn()).isEqualTo(3600L);
            assertThat(result.getUser()).isNotNull();
            assertThat(result.getUser().getUsername()).isEqualTo("testuser");

            verify(userRepository).existsByUsername("testuser");
            verify(userRepository).existsByEmail("test@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(AppUser.class));
            verify(jwtUtil).generateToken("testuser");
        }

        @Test
        @DisplayName("Should throw UserRegistrationException when username already exists")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            when(userRepository.existsByUsername(validAuthRequest.getUsername())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> authService.register(validAuthRequest))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasMessage("Username already exists");

            verify(userRepository).existsByUsername("testuser");
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(AppUser.class));
        }

        @Test
        @DisplayName("Should throw UserRegistrationException when email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            when(userRepository.existsByUsername(validAuthRequest.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(validAuthRequest.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> authService.register(validAuthRequest))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasMessage("Email already exists");

            verify(userRepository).existsByUsername("testuser");
            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(AppUser.class));
        }

        @Test
        @DisplayName("Should handle repository exception during registration")
        void shouldHandleRepositoryExceptionDuringRegistration() {
            // Given
            when(userRepository.existsByUsername(validAuthRequest.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(validAuthRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(validAuthRequest.getPassword())).thenReturn("encoded_password");
            when(userRepository.save(any(AppUser.class))).thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> authService.register(validAuthRequest))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasMessage("Failed to register user");

            verify(userRepository).save(any(AppUser.class));
        }
    }

    @Nested
    @DisplayName("User Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login user with valid credentials")
        void shouldLoginUserSuccessfully() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateToken("testuser")).thenReturn("jwt_token");
            when(jwtUtil.getExpirationTimeMs()).thenReturn(3600000L);

            // When
            AuthResponse result = authService.login(validAuthRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getAccessToken()).isEqualTo("jwt_token");
            assertThat(result.getTokenType()).isEqualTo("Bearer");
            assertThat(result.getExpiresIn()).isEqualTo(3600L);
            assertThat(result.getUser()).isNotNull();
            assertThat(result.getUser().getUsername()).isEqualTo("testuser");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtil).generateToken("testuser");
        }

        @Test
        @DisplayName("Should throw AuthenticationException for invalid credentials")
        void shouldThrowExceptionForInvalidCredentials() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // When & Then
            assertThatThrownBy(() -> authService.login(validAuthRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("Invalid username or password");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtil, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("Should handle authentication manager exception")
        void shouldHandleAuthenticationManagerException() {
            // Given
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));

            // When & Then
            assertThatThrownBy(() -> authService.login(validAuthRequest))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessage("Authentication failed");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtil, never()).generateToken(anyString());
        }
    }

    @Nested
    @DisplayName("User Existence Tests")
    class UserExistenceTests {

        @Test
        @DisplayName("Should return true when user exists by username")
        void shouldReturnTrueWhenUserExistsByUsername() {
            // Given
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            // When
            boolean result = authService.isUsernameAvailable("testuser");

            // Then
            assertThat(result).isTrue();
            verify(userRepository).existsByUsername("testuser");
        }

        @Test
        @DisplayName("Should return false when user does not exist by username")
        void shouldReturnFalseWhenUserDoesNotExistByUsername() {
            // Given
            when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

            // When
            boolean result = authService.existsByUsername("nonexistent");

            // Then
            assertThat(result).isFalse();
            verify(userRepository).existsByUsername("nonexistent");
        }

        @Test
        @DisplayName("Should return true when user exists by email")
        void shouldReturnTrueWhenUserExistsByEmail() {
            // Given
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // When
            boolean result = authService.existsByEmail("test@example.com");

            // Then
            assertThat(result).isTrue();
            verify(userRepository).existsByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should return false when user does not exist by email")
        void shouldReturnFalseWhenUserDoesNotExistByEmail() {
            // Given
            when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

            // When
            boolean result = authService.existsByEmail("nonexistent@example.com");

            // Then
            assertThat(result).isFalse();
            verify(userRepository).existsByEmail("nonexistent@example.com");
        }
    }

    @Nested
    @DisplayName("User Retrieval Tests")
    class UserRetrievalTests {

        @Test
        @DisplayName("Should return user when found by username")
        void shouldReturnUserWhenFoundByUsername() {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When
            Optional<AppUser> result = authService.findByUsername("testuser");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testUser);
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should return empty when user not found by username")
        void shouldReturnEmptyWhenUserNotFoundByUsername() {
            // Given
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

            // When
            Optional<AppUser> result = authService.findByUsername("nonexistent");

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findByUsername("nonexistent");
        }
    }
}
