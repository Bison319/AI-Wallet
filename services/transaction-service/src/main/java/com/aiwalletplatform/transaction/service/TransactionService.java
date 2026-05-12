package com.aiwalletplatform.transaction.service;

import com.aiwalletplatform.commons.entity.Transaction;
import com.aiwalletplatform.commons.exception.ResourceNotFoundException;
import com.aiwalletplatform.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction Service - Immutable Transaction Recording
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Record new transaction (immutable)
     */
    public Transaction recordTransaction(String walletId, String userId, 
                                         String description, BigDecimal amount, 
                                         Transaction.TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setWalletId(walletId);
        transaction.setUserId(userId);
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Recorded transaction: {} for wallet: {}", saved.getId(), walletId);

        // Publish event
        kafkaTemplate.send("transaction-events", "TransactionRecordedEvent", 
                "Transaction recorded: " + saved.getId());

        return saved;
    }

    /**
     * Get transaction by ID
     */
    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }

    /**
     * Get transactions for wallet
     */
    public Page<Transaction> getWalletTransactions(String walletId, Pageable pageable) {
        log.info("Fetching transactions for wallet: {}", walletId);
        return transactionRepository.findByWalletId(walletId, pageable);
    }

    /**
     * Get transactions for user
     */
    public Page<Transaction> getUserTransactions(String userId, Pageable pageable) {
        log.info("Fetching transactions for user: {}", userId);
        return transactionRepository.findByUserId(userId, pageable);
    }

    /**
     * Get total transaction amount for wallet
     */
    public Long getTotalTransactionAmount(String walletId) {
        return transactionRepository.getTotalTransactionAmount(walletId);
    }

    /**
     * Get transactions by date range
     */
    public java.util.List<Transaction> getTransactionsByDateRange(String walletId, 
                                                                   LocalDateTime start, 
                                                                   LocalDateTime end) {
        return transactionRepository.findTransactionsByDateRange(walletId, start, end);
    }
}
