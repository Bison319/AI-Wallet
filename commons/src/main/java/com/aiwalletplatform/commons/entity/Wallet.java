package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Wallet Entity - Wallet Context
 * Represents a user's wallet and balance
 */
@Entity
@Table(name = "wallets", indexes = {
    @Index(name = "idx_wallet_user_id", columnList = "user_id"),
    @Index(name = "idx_wallet_type", columnList = "wallet_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet extends BaseEntity {

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String walletType; // PRIMARY, SAVINGS, VIRTUAL_CARD

    @Column(nullable = false)
    private String walletName;

    @Column(nullable = false)
    private String currency; // INR, USD, EUR

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal ledgerBalance; // For reconciliation

    @Column(nullable = false)
    private String status; // ACTIVE, BLOCKED, SUSPENDED

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyLimit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyLimit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailySpent;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlySpent;

    @Column(nullable = false)
    private Integer failedAttempts = 0;

    @Column(nullable = false)
    private Boolean verified = false;

    private String accountNumber; // For bank settlement

    private String ifscCode; // For domestic transfers
}
