package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * FraudAlert Entity - Fraud Detection Context
 * Records suspected fraudulent activity
 */
@Entity
@Table(name = "fraud_alerts", indexes = {
    @Index(name = "idx_fraud_wallet_id", columnList = "wallet_id"),
    @Index(name = "idx_fraud_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_fraud_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert extends BaseEntity {

    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String alertType; // VELOCITY_CHECK, GEO_ANOMALY, BEHAVIOR_ANOMALY, AMOUNT_ANOMALY

    @Column(nullable = false, precision = 5, scale = 2)
    private Double riskScore; // 0.0 to 100.0

    @Column(nullable = false)
    private String status; // NEW, INVESTIGATING, RESOLVED, FALSE_POSITIVE

    @Lob
    private String analysisDetails; // JSON with detailed fraud analysis

    @Lob
    private String aiExplanation; // AI-generated explanation

    private String investigationCaseId;
    private String recommendation; // BLOCK, MONITOR, APPROVE

    @Column(nullable = false)
    private Boolean manualReview = false;

    private String reviewedBy;
}
