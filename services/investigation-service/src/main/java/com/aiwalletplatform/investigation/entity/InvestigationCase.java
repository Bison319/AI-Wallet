package com.aiwalletplatform.investigation.entity;

import com.aiwalletplatform.commons.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Investigation Case Entity
 */
@Entity
@Table(name = "investigation_cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InvestigationCase extends BaseEntity {

    @Column(unique = true)
    private String caseNumber;

    @Column
    private String walletId;

    @Column
    private String userId;

    @Column
    @Enumerated(EnumType.STRING)
    private InvestigationStatus status;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String findings;

    @Column(length = 100)
    private String assignedTo;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private CasePriority priority;

    public enum InvestigationStatus {
        OPEN,
        IN_PROGRESS,
        UNDER_REVIEW,
        CLOSED,
        ESCALATED
    }

    public enum CasePriority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
