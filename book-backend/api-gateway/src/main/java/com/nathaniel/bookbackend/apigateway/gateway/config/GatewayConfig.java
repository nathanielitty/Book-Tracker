package com.nathaniel.bookbackend.apigateway.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter filter;

    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("http://auth-service:8081"))
                .route("book-service", r -> r.path("/api/v1/books/**")
                        .filters(f -> f.rewritePath("/api/v1/books/(?<segment>.*)", "/api/books/${segment}")
                                     .filter(filter.apply(new AuthenticationFilter.Config())))
                        .uri("http://book-service:8082"))
                .route("library-service", r -> r.path("/api/v1/library/**")
                        .filters(f -> f.rewritePath("/api/v1/library/(?<segment>.*)", "/api/library/${segment}")
                                     .filter(filter.apply(new AuthenticationFilter.Config())))
                        .uri("http://library-service:8084"))
                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> f.rewritePath("/api/v1/notifications/(?<segment>.*)", "/${segment}")
                                     .filter(filter.apply(new AuthenticationFilter.Config())))
                        .uri("http://notification-service:8085"))
                .route("analytics-service", r -> r.path("/api/v1/analytics/**")
                        .filters(f -> f.rewritePath("/api/v1/analytics/(?<segment>.*)", "/${segment}")
                                     .filter(filter.apply(new AuthenticationFilter.Config())))
                        .uri("http://analytics-service:8086"))
                .build();
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

