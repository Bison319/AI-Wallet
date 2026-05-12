package com.aiwalletplatform.commons.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base event interface for all domain events
 * All events are immutable and serializable
 */
public sealed interface DomainEvent extends Serializable
    permits WalletEvent, TransactionEvent, FraudEvent, PaymentEvent, InvestigationEvent, UserEvent {

    String eventId();
    String eventType();
    String aggregateId();
    LocalDateTime timestamp();
    String correlationId();
}

// ============ WALLET EVENTS ============

public sealed interface WalletEvent extends DomainEvent
    permits WalletCreatedEvent, WalletActivatedEvent, WalletDeactivatedEvent, WalletLimitUpdatedEvent {}

/**
 * @param eventId Unique event identifier
 * @param walletId Wallet ID (aggregate root)
 * @param userId User who owns the wallet
 * @param walletType Type of wallet (PRIMARY, SAVINGS, etc.)
 * @param timestamp Event creation timestamp
 * @param correlationId For distributed tracing
 */
public record WalletCreatedEvent(
    String eventId,
    String walletId,
    String userId,
    String walletType,
    String currency,
    LocalDateTime timestamp,
    String correlationId
) implements WalletEvent {
    public WalletCreatedEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }

    @Override
    public String eventType() {
        return "WALLET_CREATED";
    }

    @Override
    public String aggregateId() {
        return walletId;
    }
}

public record WalletActivatedEvent(
    String eventId,
    String walletId,
    LocalDateTime timestamp,
    String correlationId
) implements WalletEvent {
    @Override
    public String eventType() {
        return "WALLET_ACTIVATED";
    }

    @Override
    public String aggregateId() {
        return walletId;
    }
}

public record WalletDeactivatedEvent(
    String eventId,
    String walletId,
    String reason,
    LocalDateTime timestamp,
    String correlationId
) implements WalletEvent {
    @Override
    public String eventType() {
        return "WALLET_DEACTIVATED";
    }

    @Override
    public String aggregateId() {
        return walletId;
    }
}

public record WalletLimitUpdatedEvent(
    String eventId,
    String walletId,
    java.math.BigDecimal newDailyLimit,
    java.math.BigDecimal newMonthlyLimit,
    LocalDateTime timestamp,
    String correlationId
) implements WalletEvent {
    @Override
    public String eventType() {
        return "WALLET_LIMIT_UPDATED";
    }

    @Override
    public String aggregateId() {
        return walletId;
    }
}

// ============ TRANSACTION EVENTS ============

public sealed interface TransactionEvent extends DomainEvent
    permits TransactionInitiatedEvent, TransactionProcessingEvent, TransactionCompletedEvent,
            TransactionFailedEvent, TransactionReversedEvent {}

public record TransactionInitiatedEvent(
    String eventId,
    String transactionId,
    String walletId,
    java.math.BigDecimal amount,
    String transactionType,
    String paymentMethod,
    LocalDateTime timestamp,
    String correlationId
) implements TransactionEvent {
    @Override
    public String eventType() {
        return "TRANSACTION_INITIATED";
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }
}

public record TransactionProcessingEvent(
    String eventId,
    String transactionId,
    String walletId,
    String status,
    LocalDateTime timestamp,
    String correlationId
) implements TransactionEvent {
    @Override
    public String eventType() {
        return "TRANSACTION_PROCESSING";
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }
}

public record TransactionCompletedEvent(
    String eventId,
    String transactionId,
    String walletId,
    java.math.BigDecimal amount,
    String paymentMethod,
    LocalDateTime timestamp,
    String correlationId
) implements TransactionEvent {
    @Override
    public String eventType() {
        return "TRANSACTION_COMPLETED";
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }
}

public record TransactionFailedEvent(
    String eventId,
    String transactionId,
    String walletId,
    String failureReason,
    LocalDateTime timestamp,
    String correlationId
) implements TransactionEvent {
    @Override
    public String eventType() {
        return "TRANSACTION_FAILED";
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }
}

public record TransactionReversedEvent(
    String eventId,
    String transactionId,
    String walletId,
    String originalTransactionId,
    String reversalReason,
    LocalDateTime timestamp,
    String correlationId
) implements TransactionEvent {
    @Override
    public String eventType() {
        return "TRANSACTION_REVERSED";
    }

    @Override
    public String aggregateId() {
        return transactionId;
    }
}

// ============ FRAUD EVENTS ============

public sealed interface FraudEvent extends DomainEvent
    permits SuspiciousActivityDetectedEvent, FraudAlertEscalatedEvent, AnomalyConfirmedEvent {}

public record SuspiciousActivityDetectedEvent(
    String eventId,
    String fraudAlertId,
    String walletId,
    String transactionId,
    Double riskScore,
    String alertType,
    LocalDateTime timestamp,
    String correlationId
) implements FraudEvent {
    @Override
    public String eventType() {
        return "SUSPICIOUS_ACTIVITY_DETECTED";
    }

    @Override
    public String aggregateId() {
        return fraudAlertId;
    }
}

public record FraudAlertEscalatedEvent(
    String eventId,
    String fraudAlertId,
    String walletId,
    String escalationReason,
    LocalDateTime timestamp,
    String correlationId
) implements FraudEvent {
    @Override
    public String eventType() {
        return "FRAUD_ALERT_ESCALATED";
    }

    @Override
    public String aggregateId() {
        return fraudAlertId;
    }
}

public record AnomalyConfirmedEvent(
    String eventId,
    String fraudAlertId,
    String walletId,
    Boolean confirmed,
    LocalDateTime timestamp,
    String correlationId
) implements FraudEvent {
    @Override
    public String eventType() {
        return "ANOMALY_CONFIRMED";
    }

    @Override
    public String aggregateId() {
        return fraudAlertId;
    }
}

// ============ PAYMENT EVENTS ============

public sealed interface PaymentEvent extends DomainEvent
    permits PaymentOrderCreatedEvent, PaymentSettledEvent, PaymentFailedEvent {}

public record PaymentOrderCreatedEvent(
    String eventId,
    String paymentOrderId,
    String transactionId,
    java.math.BigDecimal amount,
    LocalDateTime timestamp,
    String correlationId
) implements PaymentEvent {
    @Override
    public String eventType() {
        return "PAYMENT_ORDER_CREATED";
    }

    @Override
    public String aggregateId() {
        return paymentOrderId;
    }
}

public record PaymentSettledEvent(
    String eventId,
    String paymentOrderId,
    String transactionId,
    LocalDateTime timestamp,
    String correlationId
) implements PaymentEvent {
    @Override
    public String eventType() {
        return "PAYMENT_SETTLED";
    }

    @Override
    public String aggregateId() {
        return paymentOrderId;
    }
}

public record PaymentFailedEvent(
    String eventId,
    String paymentOrderId,
    String transactionId,
    String failureReason,
    LocalDateTime timestamp,
    String correlationId
) implements PaymentEvent {
    @Override
    public String eventType() {
        return "PAYMENT_FAILED";
    }

    @Override
    public String aggregateId() {
        return paymentOrderId;
    }
}

// ============ INVESTIGATION EVENTS ============

public sealed interface InvestigationEvent extends DomainEvent
    permits InvestigationInitiatedEvent, EvidenceCollectedEvent, InvestigationCompletedEvent {}

public record InvestigationInitiatedEvent(
    String eventId,
    String caseId,
    String walletId,
    String caseType,
    String initiatorId,
    LocalDateTime timestamp,
    String correlationId
) implements InvestigationEvent {
    @Override
    public String eventType() {
        return "INVESTIGATION_INITIATED";
    }

    @Override
    public String aggregateId() {
        return caseId;
    }
}

public record EvidenceCollectedEvent(
    String eventId,
    String caseId,
    String transactionId,
    String evidenceType,
    LocalDateTime timestamp,
    String correlationId
) implements InvestigationEvent {
    @Override
    public String eventType() {
        return "EVIDENCE_COLLECTED";
    }

    @Override
    public String aggregateId() {
        return caseId;
    }
}

public record InvestigationCompletedEvent(
    String eventId,
    String caseId,
    String walletId,
    String conclusion,
    String aiAnalysis,
    LocalDateTime timestamp,
    String correlationId
) implements InvestigationEvent {
    @Override
    public String eventType() {
        return "INVESTIGATION_COMPLETED";
    }

    @Override
    public String aggregateId() {
        return caseId;
    }
}

// ============ USER EVENTS ============

public sealed interface UserEvent extends DomainEvent
    permits UserRegisteredEvent, KYCCompletedEvent, UserSuspendedEvent {}

public record UserRegisteredEvent(
    String eventId,
    String userId,
    String email,
    LocalDateTime timestamp,
    String correlationId
) implements UserEvent {
    @Override
    public String eventType() {
        return "USER_REGISTERED";
    }

    @Override
    public String aggregateId() {
        return userId;
    }
}

public record KYCCompletedEvent(
    String eventId,
    String userId,
    String identityType,
    LocalDateTime timestamp,
    String correlationId
) implements UserEvent {
    @Override
    public String eventType() {
        return "KYC_COMPLETED";
    }

    @Override
    public String aggregateId() {
        return userId;
    }
}

public record UserSuspendedEvent(
    String eventId,
    String userId,
    String reason,
    LocalDateTime timestamp,
    String correlationId
) implements UserEvent {
    @Override
    public String eventType() {
        return "USER_SUSPENDED";
    }

    @Override
    public String aggregateId() {
        return userId;
    }
}
