package com.aiwalletplatform.commons.util;

import java.util.UUID;

/**
 * Correlation ID management for distributed tracing
 * Implements OpenTelemetry trace context propagation
 */
public class CorrelationIdContext {
    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

    public static void setCorrelationId(String id) {
        correlationId.set(id);
    }

    public static String getCorrelationId() {
        String id = correlationId.get();
        if (id == null) {
            id = UUID.randomUUID().toString();
            correlationId.set(id);
        }
        return id;
    }

    public static void clear() {
        correlationId.remove();
    }
}

/**
 * Common utility functions
 */
public class CommonUtils {
    private CommonUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Generate a correlation ID following W3C Trace Context format
     * @return correlation ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a unique transaction reference
     * @return reference number
     */
    public static String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generate a case reference for investigations
     * @return case reference
     */
    public static String generateCaseReference() {
        return "CASE" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Mask sensitive data for logging
     * @param value sensitive value
     * @return masked value
     */
    public static String maskSensitiveData(String value) {
        if (value == null || value.length() < 4) {
            return "****";
        }
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }

    /**
     * Mask email for display
     * @param email email address
     * @return masked email
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        if (localPart.length() < 2) {
            return "****@" + email.substring(atIndex + 1);
        }
        String masked = localPart.charAt(0) + "****" + localPart.charAt(localPart.length() - 1);
        return masked + "@" + email.substring(atIndex + 1);
    }

    /**
     * Check if amount is valid
     * @param amount amount to check
     * @return true if valid
     */
    public static boolean isValidAmount(java.math.BigDecimal amount) {
        return amount != null && amount.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    /**
     * Check if transaction is suspicious based on risk score
     * @param riskScore risk score (0-100)
     * @return true if suspicious
     */
    public static boolean isSuspicious(Double riskScore) {
        return riskScore != null && riskScore >= 70.0;
    }

    /**
     * Check if transaction requires high-risk review
     * @param riskScore risk score (0-100)
     * @return true if high risk
     */
    public static boolean isHighRisk(Double riskScore) {
        return riskScore != null && riskScore >= 85.0;
    }

    /**
     * Calculate transaction fee
     * @param amount transaction amount
     * @param feePercentage fee percentage
     * @return fee amount
     */
    public static java.math.BigDecimal calculateFee(java.math.BigDecimal amount, Double feePercentage) {
        return amount.multiply(java.math.BigDecimal.valueOf(feePercentage / 100.0))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}

/**
 * Request context holder for Spring
 */
public class RequestContext {
    private String userId;
    private String walletId;
    private String correlationId;
    private String sourceIp;
    private String userAgent;

    public RequestContext() {}

    public RequestContext(String userId, String correlationId, String sourceIp) {
        this.userId = userId;
        this.correlationId = correlationId;
        this.sourceIp = sourceIp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}

/**
 * Request context holder for ThreadLocal
 */
public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    public static void setContext(RequestContext context) {
        contextHolder.set(context);
    }

    public static RequestContext getContext() {
        RequestContext context = contextHolder.get();
        if (context == null) {
            context = new RequestContext();
            contextHolder.set(context);
        }
        return context;
    }

    public static void clear() {
        contextHolder.remove();
    }
}
