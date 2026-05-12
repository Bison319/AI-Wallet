package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User Entity - Identity Context
 * Represents a user in the wallet platform
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_phone", columnList = "phone")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String identityType; // AADHAR, PAN, PASSPORT

    @Column(nullable = false, unique = true)
    private String identityNumber;

    @Column(nullable = false)
    private String accountStatus; // ACTIVE, SUSPENDED, VERIFIED, UNVERIFIED

    @Column(nullable = false)
    private Boolean kycCompleted = false;

    @Column(nullable = false)
    private Boolean amlVerified = false;

    @Lob
    private String kycData; // JSON formatted KYC details

    private String riskProfile; // LOW, MEDIUM, HIGH

    private LocalDateTime lastLoginAt;
}
