package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InvestigationCase Entity - Investigation Context
 * Records compliance and forensic investigations
 */
@Entity
@Table(name = "investigation_cases", indexes = {
    @Index(name = "idx_investigation_user_id", columnList = "initiator_id"),
    @Index(name = "idx_investigation_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestigationCase extends BaseEntity {

    @Column(nullable = false)
    private String caseType; // FRAUD_INVESTIGATION, COMPLIANCE_CHECK, AML_MONITORING

    @Column(nullable = false)
    private String status; // OPEN, IN_PROGRESS, RESOLVED, ESCALATED

    @Column(nullable = false)
    private String walletId;

    private String relatedWalletIds; // CSV for related wallets

    @Column(nullable = false)
    private String initiatorId;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    @Lob
    private String description;

    @Lob
    private String queryContext; // Natural language query

    @Lob
    private String ragResults; // RAG-retrieved context

    @Lob
    private String aiAnalysis; // AI-generated investigation report

    @Lob
    private String findings; // Investigator findings

    private String assignedTo;
    private Integer evidenceCount = 0;

    @Column(nullable = false)
    private Boolean complianceReady = false;
}
