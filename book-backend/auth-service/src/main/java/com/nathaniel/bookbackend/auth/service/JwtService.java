package com.nathaniel.bookbackend.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    public String generateToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId)  // Changed from setSubject() to subject()
                .claim("username", username)
                .issuedAt(now)  // Changed from setIssuedAt() to issuedAt()
                .expiration(expiryDate)  // Changed from setExpiration() to expiration()
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), Jwts.SIG.HS512)  // Changed SignatureAlgorithm to Jwts.SIG
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))  // Changed from setSigningKey() to verifyWith()
                    .build()
                    .parseSignedClaims(token)  // Changed from parseClaimsJws() to parseSignedClaims()
                    .getPayload();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | 
               SignatureException | IllegalArgumentException e) {
            return null;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }
}