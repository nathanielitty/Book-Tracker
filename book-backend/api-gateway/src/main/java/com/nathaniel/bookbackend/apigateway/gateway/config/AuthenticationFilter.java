package com.nathaniel.bookbackend.apigateway.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient webClient;

    public AuthenticationFilter() {
        super(Config.class);
        this.webClient = WebClient.builder().build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            
            // Handle OPTIONS requests (CORS preflight) without forwarding to backend
            if ("OPTIONS".equals(method)) {
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
                exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
                exchange.getResponse().getHeaders().add("Access-Control-Max-Age", "3600");
                return exchange.getResponse().setComplete();
            }
            
            // Skip authentication for public endpoints
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }
            
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return Mono.error(new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization information"));
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                return Mono.error(new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect authorization structure"));
            }

            return webClient
                    .get()
                    .uri("http://auth-service:8081/api/v1/auth/validate?token=" + parts[1])
                    .retrieve().bodyToMono(Void.class)
                    .then(chain.filter(exchange))
                    .onErrorResume(e -> Mono.error(new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authorized")));
        };
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.equals("/api/v1/auth/login") || 
               path.equals("/api/v1/auth/register") ||
               path.equals("/api/v1/auth/validate");
    }

    public static class Config {
        //Put the configuration properties here
    }
}
