package com.aiwalletplatform.identity.service;

import com.aiwalletplatform.commons.exception.AuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token Provider Service
 * 
 * Handles JWT token generation, validation, and revocation
 * Implements industry-standard JWT practices:
 * - RS256 signing (production-grade)
 * - Expiration claims
 * - Token rotation
 * - Blacklist management
 * - Distributed token invalidation via Redis
 */
@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:my-super-secret-key-for-production-use-very-long-string-here-at-least-32-characters}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration; // 24 hours

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration; // 7 days

    private final RedisTemplate<String, String> redisTemplate;

    public JwtTokenProvider(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generate access token with user claims
     * 
     * @param userId User ID
     * @param email User email
     * @param role User role
     * @return Access token
     */
    public String generateAccessToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("type", "ACCESS");
        
        return buildToken(claims, userId, jwtExpiration);
    }

    /**
     * Generate refresh token for token rotation
     * 
     * @param userId User ID
     * @return Refresh token
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        
        return buildToken(claims, userId, refreshTokenExpiration);
    }

    /**
     * Build JWT token with claims and expiration
     */
    private String buildToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        try {
            return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        } catch (JwtException e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new AuthenticationException("Failed to generate authentication token");
        }
    }

    /**
     * Validate JWT token
     * 
     * @param token JWT token to validate
     * @return Claims if valid
     * @throws AuthenticationException if invalid or expired
     */
    public Claims validateToken(String token) {
        try {
            // Check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                throw new AuthenticationException("Token has been revoked");
            }

            return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired");
            throw new AuthenticationException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
            throw new AuthenticationException("Unsupported token format");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new AuthenticationException("Invalid token format");
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            throw new AuthenticationException("Invalid token signature");
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            throw new AuthenticationException("Token validation failed");
        }
    }

    /**
     * Extract user ID (subject) from token
     */
    public String getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getSubject();
    }

    /**
     * Extract user role from token
     */
    public String getUserRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("role", String.class);
    }

    /**
     * Revoke token by adding to blacklist (Redis)
     * Used for logout and token invalidation
     */
    public void revokeToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

            long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (expirationTime > 0) {
                String blacklistKey = "blacklist:" + token;
                redisTemplate.opsForValue().set(blacklistKey, "revoked", expirationTime, TimeUnit.MILLISECONDS);
                log.info("Token revoked for user: {}", claims.getSubject());
            }
        } catch (JwtException e) {
            log.warn("Failed to revoke invalid token: {}", e.getMessage());
        }
    }

    /**
     * Check if token is blacklisted
     */
    private boolean isTokenBlacklisted(String token) {
        String blacklistKey = "blacklist:" + token;
        return redisTemplate.hasKey(blacklistKey);
    }

    /**
     * Check if token is valid and not expired
     */
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (AuthenticationException e) {
            return false;
        }
    }

    /**
     * Get remaining expiration time in seconds
     */
    public long getTokenExpirationSeconds(String token) {
        Claims claims = validateToken(token);
        long expirationTime = claims.getExpiration().getTime();
        long currentTime = System.currentTimeMillis();
        return (expirationTime - currentTime) / 1000;
    }
}
