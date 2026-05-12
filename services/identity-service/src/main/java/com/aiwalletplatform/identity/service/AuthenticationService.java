package com.aiwalletplatform.identity.service;

import com.aiwalletplatform.commons.exception.AuthenticationException;
import com.aiwalletplatform.commons.exception.InvalidRequestException;
import com.aiwalletplatform.commons.util.CorrelationIdContext;
import com.aiwalletplatform.identity.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * 
 * Orchestrates:
 * - User registration
 * - User login
 * - Token generation and refresh
 * - Token validation and revocation
 * - RBAC
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthService userAuthService;

    /**
     * Register new user
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        log.info("[{}] Registering new user with email: {}", correlationId, request.email());

        // Check if user already exists
        if (userAuthService.existsByEmail(request.email())) {
            log.warn("[{}] User already exists with email: {}", correlationId, request.email());
            throw new InvalidRequestException("User with this email already exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.password());

        // Create user
        var user = userAuthService.createUser(
            request.email(),
            encodedPassword,
            request.firstName(),
            request.lastName(),
            request.phone()
        );

        log.info("[{}] User registered successfully with ID: {}", correlationId, user.id());
        
        return user;
    }

    /**
     * User login
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        log.info("[{}] Login attempt for email: {}", correlationId, request.email());

        // Find user by email
        var user = userAuthService.findByEmail(request.email())
            .orElseThrow(() -> {
                log.warn("[{}] User not found with email: {}", correlationId, request.email());
                return new AuthenticationException("Invalid email or password");
            });

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            log.warn("[{}] Invalid password for user: {}", correlationId, request.email());
            throw new AuthenticationException("Invalid email or password");
        }

        // Check account status
        if (!"ACTIVE".equals(user.accountStatus())) {
            log.warn("[{}] Account not active for user: {}", correlationId, request.email());
            throw new AuthenticationException("Account is not active");
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.id(), user.email(), user.role());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        log.info("[{}] User logged in successfully: {}", correlationId, user.email());

        return new LoginResponse(
            user.id(),
            user.email(),
            user.role(),
            accessToken,
            refreshToken,
            24 * 60 * 60,  // 24 hours in seconds
            "Bearer"
        );
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional(readOnly = true)
    public LoginResponse refreshAccessToken(String refreshToken) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        log.debug("[{}] Refreshing access token", correlationId);

        // Validate refresh token
        var claims = jwtTokenProvider.validateToken(refreshToken);
        String tokenType = claims.get("type", String.class);
        
        if (!"REFRESH".equals(tokenType)) {
            log.warn("[{}] Invalid token type for refresh: {}", correlationId, tokenType);
            throw new AuthenticationException("Invalid token type for refresh");
        }

        String userId = claims.getSubject();
        
        // Fetch user details
        var user = userAuthService.findById(userId)
            .orElseThrow(() -> {
                log.warn("[{}] User not found with ID: {}", correlationId, userId);
                return new AuthenticationException("User not found");
            });

        // Generate new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.id(), user.email(), user.role());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id());

        log.info("[{}] Access token refreshed for user: {}", correlationId, user.email());

        return new LoginResponse(
            user.id(),
            user.email(),
            user.role(),
            newAccessToken,
            newRefreshToken,
            24 * 60 * 60,
            "Bearer"
        );
    }

    /**
     * Logout and revoke token
     */
    @Transactional
    public void logout(String token) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        log.info("[{}] Logout requested", correlationId);

        jwtTokenProvider.revokeToken(token);
        
        log.info("[{}] User logged out and token revoked", correlationId);
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.isTokenValid(token);
    }
}
