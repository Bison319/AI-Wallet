package com.aiwalletplatform.commons.exception;

/**
 * Base exception for all application exceptions
 */
public abstract class ApplicationException extends RuntimeException {
    protected final String errorCode;

    public ApplicationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

/**
 * Resource not found exception
 */
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String resource, String identifier) {
        super(String.format("%s not found: %s", resource, identifier), "RESOURCE_NOT_FOUND");
    }
}

/**
 * Invalid request exception
 */
public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String message) {
        super(message, "INVALID_REQUEST");
    }
}

/**
 * Business rule violation exception
 */
public class BusinessRuleViolationException extends ApplicationException {
    public BusinessRuleViolationException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}

/**
 * Authentication failed exception
 */
public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_FAILED");
    }
}

/**
 * Authorization failed exception
 */
public class AuthorizationException extends ApplicationException {
    public AuthorizationException(String message) {
        super(message, "AUTHORIZATION_FAILED");
    }
}

/**
 * Insufficient balance exception
 */
public class InsufficientBalanceException extends ApplicationException {
    public InsufficientBalanceException(String walletId, java.math.BigDecimal required, java.math.BigDecimal available) {
        super(String.format("Insufficient balance in wallet %s. Required: %s, Available: %s",
            walletId, required, available), "INSUFFICIENT_BALANCE");
    }
}

/**
 * Wallet limit exceeded exception
 */
public class WalletLimitExceededException extends ApplicationException {
    public WalletLimitExceededException(String limitType, java.math.BigDecimal limit) {
        super(String.format("%s limit exceeded: %s", limitType, limit), "WALLET_LIMIT_EXCEEDED");
    }
}

/**
 * Fraud detected exception
 */
public class FraudDetectedException extends ApplicationException {
    public FraudDetectedException(String fraudAlertId, Double riskScore) {
        super(String.format("Fraud detected. Alert ID: %s, Risk Score: %s", fraudAlertId, riskScore), "FRAUD_DETECTED");
    }
}

/**
 * Transaction processing exception
 */
public class TransactionProcessingException extends ApplicationException {
    public TransactionProcessingException(String transactionId, String reason) {
        super(String.format("Failed to process transaction %s: %s", transactionId, reason), "TRANSACTION_PROCESSING_ERROR");
    }
}

/**
 * External service exception
 */
public class ExternalServiceException extends ApplicationException {
    public ExternalServiceException(String service, String message) {
        super(String.format("External service error from %s: %s", service, message), "EXTERNAL_SERVICE_ERROR");
    }
}

/**
 * Data consistency exception
 */
public class DataConsistencyException extends ApplicationException {
    public DataConsistencyException(String message) {
        super(message, "DATA_CONSISTENCY_ERROR");
    }
}

/**
 * AI processing exception
 */
public class AIProcessingException extends ApplicationException {
    public AIProcessingException(String message) {
        super(message, "AI_PROCESSING_ERROR");
    }

    public AIProcessingException(String message, Throwable cause) {
        super(message, "AI_PROCESSING_ERROR", cause);
    }
}

/**
 * RAG query exception
 */
public class RAGQueryException extends ApplicationException {
    public RAGQueryException(String message) {
        super(message, "RAG_QUERY_ERROR");
    }
}
