package com.aiwalletplatform.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * UserAuth Entity - Local authentication user
 * Separate from the User entity in User Service
 * Handles authentication-specific data
 */
@Entity
@Table(name = "user_auth", indexes = {
    @Index(name = "idx_user_auth_email", columnList = "email", unique = true),
    @Index(name = "idx_user_auth_phone", columnList = "phone")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {

    @Id
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String role = "USER"; // USER, ADMIN, SUPPORT

    @Column(nullable = false)
    private String accountStatus = "ACTIVE"; // ACTIVE, SUSPENDED, INACTIVE

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Boolean credentialsNotExpired = true;

    @Column(nullable = false)
    private Boolean accountNotLocked = true;

    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Version
    private Long version;
}
