package com.piramal.sukrtya.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationTime = 1000 * 60 * 60; // 1 hour

    // Generate a JWT token
    public String generateToken(String username, Map<String, Object> claims) {
        logger.info("Generating token for user: {}", username);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();

        logger.info("Token successfully generated for user: {}", username);
        return token;
    }

    // Validate the token
    public boolean validateToken(String token, String username) {
        logger.info("Validating token for user: {}", username);

        try {
            String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);
            logger.info("Token validation result for user {}: {}", username, isValid);
            return isValid;
        } catch (JwtException e) {
            logger.error("Token validation failed for user: {}. Error: {}", username, e.getMessage());
            return false;
        }
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        logger.info("Extracting username from token");

        try {
            String username = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody().getSubject();
            logger.info("Username extracted from token: {}", username);
            return username;
        } catch (JwtException e) {
            logger.error("Failed to extract username from token. Error: {}", e.getMessage());
            throw new RuntimeException("Failed to extract username from token", e);
        }
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from the token
    private Date extractExpiration(String token) {
        logger.info("Extracting expiration date from token");

        try {
            Date expiration = Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token).getBody().getExpiration();
            logger.info("Expiration date extracted from token: {}", expiration);
            return expiration;
        } catch (JwtException e) {
            logger.error("Failed to extract expiration from token. Error: {}", e.getMessage());
            throw new RuntimeException("Failed to extract expiration from token", e);
        }
    }
}
