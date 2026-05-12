package com.aiwalletplatform.transaction.controller;

import com.aiwalletplatform.commons.dto.ApiResponse;
import com.aiwalletplatform.commons.entity.Transaction;
import com.aiwalletplatform.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction REST Controller
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Record transaction
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> recordTransaction(
            @RequestParam String walletId,
            @RequestParam String userId,
            @RequestParam String description,
            @RequestParam BigDecimal amount,
            @RequestParam Transaction.TransactionType type) {
        Transaction transaction = transactionService.recordTransaction(
                walletId, userId, description, amount, type);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransaction(transactionId);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }

    /**
     * Get wallet transactions
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getWalletTransactions(
            @PathVariable String walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getWalletTransactions(walletId, pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * Get user transactions
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getUserTransactions(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    /**
     * Get total transaction amount
     */
    @GetMapping("/wallet/{walletId}/total")
    public ResponseEntity<ApiResponse<Long>> getTotalAmount(@PathVariable String walletId) {
        Long total = transactionService.getTotalTransactionAmount(walletId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}
