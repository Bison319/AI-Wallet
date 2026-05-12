package com.aiwalletplatform.commons.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity for all domain models
 * Provides common fields and audit capabilities
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public abstract class BaseEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()")
    protected String id = UUID.randomUUID().toString();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    protected LocalDateTime updatedAt;

    @Column(nullable = false)
    protected String createdBy = "SYSTEM";

    @Column(nullable = false)
    protected String updatedBy = "SYSTEM";

    @Version
    protected Long version;

    @Column(nullable = false)
    protected Boolean deleted = false;
}
