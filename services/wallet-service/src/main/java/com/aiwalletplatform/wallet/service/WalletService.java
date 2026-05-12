package com.aiwalletplatform.wallet.service;

import com.aiwalletplatform.commons.entity.Wallet;
import com.aiwalletplatform.commons.event.DomainEvent;
import com.aiwalletplatform.commons.exception.BusinessRuleViolationException;
import com.aiwalletplatform.commons.exception.InsufficientBalanceException;
import com.aiwalletplatform.commons.exception.ResourceNotFoundException;
import com.aiwalletplatform.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Wallet Service - Business Logic for Wallet Management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Create new wallet for user
     */
    @CacheEvict(value = "wallets", key = "#userId")
    public Wallet createWallet(String userId, String walletName) {
        // Check if user already has a wallet
        if (walletRepository.countByUserId(userId) > 0) {
            throw new BusinessRuleViolationException("User already has a wallet");
        }

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID().toString());
        wallet.setUserId(userId);
        wallet.setWalletName(walletName);
        wallet.setWalletNumber(generateWalletNumber());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("USD");
        wallet.setStatus(Wallet.WalletStatus.ACTIVE);
        wallet.setCreatedAt(LocalDateTime.now());

        Wallet saved = walletRepository.save(wallet);
        log.info("Created wallet for userId: {}, walletId: {}", userId, saved.getId());

        // Publish event
        kafkaTemplate.send("wallet-events", "WalletCreatedEvent", 
                "Wallet created for user: " + userId);

        return saved;
    }

    /**
     * Get wallet by ID
     */
    @Cacheable(value = "wallets", key = "#walletId")
    public Wallet getWallet(String walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
    }

    /**
     * Get wallet by user ID
     */
    @Cacheable(value = "wallets", key = "'user:' + #userId")
    public Wallet getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
    }

    /**
     * Credit wallet balance
     */
    @CacheEvict(value = "wallets", key = "#walletId")
    public Wallet creditBalance(String walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Credit amount must be positive");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet updated = walletRepository.save(wallet);

        log.info("Credited wallet: {} with amount: {}", walletId, amount);
        kafkaTemplate.send("wallet-events", "BalanceCreditedEvent", 
                "Balance credited: " + amount);

        return updated;
    }

    /**
     * Debit wallet balance
     */
    @CacheEvict(value = "wallets", key = "#walletId")
    public Wallet debitBalance(String walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Debit amount must be positive");
        }

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available: " + 
                    wallet.getBalance() + ", Required: " + amount);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        Wallet updated = walletRepository.save(wallet);

        log.info("Debited wallet: {} with amount: {}", walletId, amount);
        kafkaTemplate.send("wallet-events", "BalanceDebitedEvent", 
                "Balance debited: " + amount);

        return updated;
    }

    /**
     * Update wallet status
     */
    @CacheEvict(value = "wallets", key = "#walletId")
    public Wallet updateWalletStatus(String walletId, Wallet.WalletStatus status) {
        Wallet wallet = getWallet(walletId);
        wallet.setStatus(status);

        Wallet updated = walletRepository.save(wallet);
        log.info("Updated wallet: {} status to: {}", walletId, status);

        return updated;
    }

    /**
     * Generate unique wallet number
     */
    private String generateWalletNumber() {
        return "WL" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }
}
