package com.aiwalletplatform.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * User DTOs - User bounded context
 */
public record UserDTO(
    @JsonProperty("id") String id,
    @JsonProperty("email") String email,
    @JsonProperty("phone") String phone,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("kycCompleted") Boolean kycCompleted,
    @JsonProperty("accountStatus") String accountStatus
) {}

public record CreateUserRequest(
    @JsonProperty("email") String email,
    @JsonProperty("phone") String phone,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName,
    @JsonProperty("identityType") String identityType,
    @JsonProperty("identityNumber") String identityNumber
) {}

/**
 * Wallet DTOs - Wallet bounded context
 */
public record WalletDTO(
    @JsonProperty("id") String id,
    @JsonProperty("userId") String userId,
    @JsonProperty("walletType") String walletType,
    @JsonProperty("balance") BigDecimal balance,
    @JsonProperty("status") String status,
    @JsonProperty("currency") String currency
) {}

public record CreateWalletRequest(
    @JsonProperty("userId") String userId,
    @JsonProperty("walletType") String walletType,
    @JsonProperty("walletName") String walletName,
    @JsonProperty("currency") String currency,
    @JsonProperty("dailyLimit") BigDecimal dailyLimit,
    @JsonProperty("monthlyLimit") BigDecimal monthlyLimit
) {}

public record UpdateWalletLimitRequest(
    @JsonProperty("dailyLimit") BigDecimal dailyLimit,
    @JsonProperty("monthlyLimit") BigDecimal monthlyLimit
) {}

/**
 * Transaction DTOs - Transaction bounded context
 */
public record TransactionDTO(
    @JsonProperty("id") String id,
    @JsonProperty("walletId") String walletId,
    @JsonProperty("amount") BigDecimal amount,
    @JsonProperty("transactionType") String transactionType,
    @JsonProperty("status") String status,
    @JsonProperty("riskScore") String riskScore,
    @JsonProperty("suspicious") Boolean suspicious,
    @JsonProperty("timestamp") LocalDateTime timestamp
) {}

public record CreateTransactionRequest(
    @JsonProperty("walletId") String walletId,
    @JsonProperty("amount") BigDecimal amount,
    @JsonProperty("transactionType") String transactionType,
    @JsonProperty("paymentMethod") String paymentMethod,
    @JsonProperty("merchantId") String merchantId,
    @JsonProperty("description") String description
) {}

/**
 * Fraud Detection DTOs
 */
public record FraudAlertDTO(
    @JsonProperty("id") String id,
    @JsonProperty("transactionId") String transactionId,
    @JsonProperty("riskScore") Double riskScore,
    @JsonProperty("alertType") String alertType,
    @JsonProperty("status") String status,
    @JsonProperty("aiExplanation") String aiExplanation
) {}

/**
 * Investigation DTOs
 */
public record InvestigationCaseDTO(
    @JsonProperty("id") String id,
    @JsonProperty("caseType") String caseType,
    @JsonProperty("status") String status,
    @JsonProperty("walletId") String walletId,
    @JsonProperty("priority") String priority,
    @JsonProperty("aiAnalysis") String aiAnalysis
) {}

public record InvestigationQueryRequest(
    @JsonProperty("walletId") String walletId,
    @JsonProperty("query") String query,
    @JsonProperty("priority") String priority
) {}

/**
 * AI Assistant DTOs
 */
public record ChatMessageRequest(
    @JsonProperty("userId") String userId,
    @JsonProperty("walletId") String walletId,
    @JsonProperty("message") String message,
    @JsonProperty("conversationId") String conversationId
) {}

public record ChatMessageResponse(
    @JsonProperty("conversationId") String conversationId,
    @JsonProperty("response") String response,
    @JsonProperty("confidence") Double confidence,
    @JsonProperty("sources") java.util.List<String> sources
) {}

/**
 * Common Response DTOs
 */
public record ApiResponse<T>(
    @JsonProperty("success") Boolean success,
    @JsonProperty("data") T data,
    @JsonProperty("error") String error,
    @JsonProperty("timestamp") LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, error, LocalDateTime.now());
    }
}

/**
 * Pagination DTOs
 */
public record PageRequest(
    @JsonProperty("page") Integer page,
    @JsonProperty("size") Integer size,
    @JsonProperty("sort") String sort
) {}

public record PageResponse<T>(
    @JsonProperty("content") java.util.List<T> content,
    @JsonProperty("totalElements") Long totalElements,
    @JsonProperty("totalPages") Integer totalPages,
    @JsonProperty("currentPage") Integer currentPage,
    @JsonProperty("pageSize") Integer pageSize
) {}
