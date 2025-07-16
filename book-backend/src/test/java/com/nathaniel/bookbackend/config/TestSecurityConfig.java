package com.nathaniel.bookbackend.config;

import com.nathaniel.bookbackend.security.JwtUtil;
import com.nathaniel.bookbackend.security.JwtAuthorizationFilter;
import com.nathaniel.bookbackend.security.AppUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test configuration to provide beans required for web layer tests
 * This configuration is automatically detected by Spring's test context
 * and provides mock beans for security components
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
