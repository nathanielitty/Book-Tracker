package com.nathaniel.bookbackend.library.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Disable CORS completely - let API Gateway handle it
        registry.addMapping("/**").allowedOrigins().allowedMethods().allowedHeaders();
    }
} 