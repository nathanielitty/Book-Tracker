package com.nathaniel.bookbackend.apigateway.gateway.config;

import org.springframework.context.annotation.Configuration;

// @Configuration - Temporarily disabled to test without CORS
public class CorsGlobalConfiguration {

    // @Bean
    // public CorsWebFilter corsWebFilter() {
    //     CorsConfiguration corsConfig = new CorsConfiguration();
    //     corsConfig.setAllowCredentials(false);
    //     corsConfig.addAllowedOriginPattern("*");
    //     corsConfig.addAllowedHeader("*");
    //     corsConfig.addAllowedMethod("*");

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", corsConfig);

    //     return new CorsWebFilter(source);
    // }
}
