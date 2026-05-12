package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuditLog Entity - Audit Context
 * Immutable audit trail for compliance and security
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_entity_id", columnList = "entity_id"),
    @Index(name = "idx_audit_entity_type", columnList = "entity_type"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String entityType; // USER, WALLET, TRANSACTION, FRAUD_ALERT

    @Column(nullable = false)
    private String entityId;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, VIEW

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String sourceIp;

    @Lob
    private String previousState; // JSON of previous values

    @Lob
    private String newState; // JSON of new values

    @Lob
    private String changeDescription;

    @Column(nullable = false)
    private String status; // SUCCESS, FAILURE

    private String failureReason;

    private String correlationId;

    @Column(nullable = false)
    private Boolean sensitive = false; // For PII handling
}
