package com.aiwalletplatform.identity.controller;

import com.aiwalletplatform.commons.dto.ApiResponse;
import com.aiwalletplatform.identity.dto.LoginRequest;
import com.aiwalletplatform.identity.dto.LoginResponse;
import com.aiwalletplatform.identity.dto.RegisterRequest;
import com.aiwalletplatform.identity.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Authentication Controller
 * 
 * Endpoints:
 * - POST /api/v1/auth/register - User registration
 * - POST /api/v1/auth/login - User login
 * - POST /api/v1/auth/refresh - Refresh access token
 * - POST /api/v1/auth/logout - Logout and revoke token
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Register new user
     * 
     * @param request Registration request with email, password, name
     * @return User created response
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account with email and password")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration requested for email: {}", request.email());
        
        var user = authenticationService.register(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(user));
    }

    /**
     * User login
     * 
     * @param request Login request with email and password
     * @return JWT access token and refresh token
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login requested for email: {}", request.email());
        
        LoginResponse loginResponse = authenticationService.login(request);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(loginResponse));
    }

    /**
     * Refresh access token
     * 
     * @param refreshToken Refresh token from login response
     * @return New access token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Use refresh token to get a new access token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestParam String refreshToken) {
        log.debug("Token refresh requested");
        
        LoginResponse loginResponse = authenticationService.refreshAccessToken(refreshToken);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(loginResponse));
    }

    /**
     * Logout and revoke token
     * 
     * @param authHeader Authorization header with Bearer token
     * @return Logout confirmation
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke current JWT token")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authHeader) {
        log.debug("Logout requested");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authenticationService.logout(token);
        }
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success("Logged out successfully"));
    }

    /**
     * Validate token
     * 
     * @param token JWT token to validate
     * @return Validation result
     */
    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Check if JWT token is valid")
    public ResponseEntity<ApiResponse<?>> validateToken(@RequestParam String token) {
        boolean isValid = authenticationService.validateToken(token);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(isValid ? "Token is valid" : "Token is invalid"));
    }
}
