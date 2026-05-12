package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Entity - Transaction Context
 * Immutable transaction record for audit trail
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_wallet_id", columnList = "wallet_id"),
    @Index(name = "idx_transaction_status", columnList = "status"),
    @Index(name = "idx_transaction_created_at", columnList = "created_at"),
    @Index(name = "idx_transaction_merchant", columnList = "merchant_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String transactionType; // DEBIT, CREDIT, TRANSFER, REFUND

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal fee;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED, REVERSED

    @Column(nullable = false)
    private String description;

    private String merchantId;
    private String merchantName;
    private String merchantCategory;

    @Column(nullable = false)
    private String paymentMethod; // CARD, UPI, BANK_TRANSFER, WALLET

    private String referenceNumber;
    private String correlationId; // For distributed tracing

    // Fraud detection fields
    private String riskScore; // LOW, MEDIUM, HIGH
    private Boolean fraudChecked = false;
    private Boolean suspicious = false;

    // Geo information
    private String deviceLocation;
    private String transactionLocation;
    private Double latitude;
    private Double longitude;

    private LocalDateTime processedAt;
}
