package com.aiwalletplatform.audit.service;

import com.aiwalletplatform.commons.entity.AuditLog;
import com.aiwalletplatform.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Audit Service - Immutable Audit Trail Management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Record audit log (immutable)
     */
    public AuditLog recordAuditLog(String userId, String action, String entityType, 
                                    String entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setId(UUID.randomUUID().toString());
        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());

        AuditLog saved = auditLogRepository.save(auditLog);
        log.info("Audit log recorded - Action: {}, Entity: {}, User: {}", action, entityType, userId);

        return saved;
    }

    /**
     * Get audit logs for user
     */
    public Page<AuditLog> getUserAuditLogs(String userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable);
    }

    /**
     * Get audit logs for entity type
     */
    public Page<AuditLog> getEntityAuditLogs(String entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable);
    }

    /**
     * Listen to domain events and create audit logs
     */
    @KafkaListener(topics = {"wallet-events", "transaction-events", "fraud-events", 
                            "payment-events", "investigation-events"}, 
                  groupId = "audit-service")
    public void handleDomainEvent(String event) {
        log.info("Recording audit event: {}", event);
        // Parse event and create audit log
    }
}
