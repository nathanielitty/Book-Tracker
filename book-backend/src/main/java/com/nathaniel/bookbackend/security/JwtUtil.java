package com.nathaniel.bookbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class following Netflix security standards
 * Includes comprehensive token management and validation
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:your-256-bit-secret-key-here-must-be-long-enough-for-hs256}")
    private String secret;
    
    @Value("${jwt.expiration.ms:3600000}") // 1 hour default
    private long expirationMs;
    
    @Value("${jwt.refresh.expiration.ms:86400000}") // 24 hours default
    private long refreshExpirationMs;
    
    /**
     * Generate JWT token for UserDetails
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }
    
    /**
     * Generate JWT token for username
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpirationMs);
    }
    
    /**
     * Create JWT token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return createToken(claims, subject, expirationMs);
    }
    
    /**
     * Create JWT token with custom expiration
     */
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract issued at date from token
     */
    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }
    
    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Validate token against user details
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Validate token format and signature
     */
    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get signing key for JWT
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Get token expiration time in milliseconds
     */
    public long getExpirationTimeMs() {
        return expirationMs;
    }
    
    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationTimeMs() {
        return refreshExpirationMs;
    }
}