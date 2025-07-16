package com.nathaniel.bookbackend.apigateway.gateway.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsConfig {
    // CORS is now handled directly in AuthenticationFilter to avoid duplicate headers
}
