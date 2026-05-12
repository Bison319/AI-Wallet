package com.aiwalletplatform.identity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Authentication DTOs
 */

public record RegisterRequest(
    @JsonProperty("email")
    @Email(message = "Email should be valid")
    String email,

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    String password,

    @JsonProperty("firstName")
    @NotBlank(message = "First name cannot be blank")
    String firstName,

    @JsonProperty("lastName")
    @NotBlank(message = "Last name cannot be blank")
    String lastName,

    @JsonProperty("phone")
    @NotBlank(message = "Phone cannot be blank")
    String phone
) {}

public record LoginRequest(
    @JsonProperty("email")
    @Email(message = "Email should be valid")
    String email,

    @JsonProperty("password")
    @NotBlank(message = "Password cannot be blank")
    String password
) {}

public record LoginResponse(
    @JsonProperty("userId") String userId,
    @JsonProperty("email") String email,
    @JsonProperty("role") String role,
    @JsonProperty("accessToken") String accessToken,
    @JsonProperty("refreshToken") String refreshToken,
    @JsonProperty("expiresIn") Long expiresIn,
    @JsonProperty("tokenType") String tokenType
) {}

public record UserResponse(
    @JsonProperty("id") String id,
    @JsonProperty("email") String email,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("phone") String phone,
    @JsonProperty("role") String role,
    @JsonProperty("accountStatus") String accountStatus
) {}
