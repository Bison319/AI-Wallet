package com.aiwalletplatform.wallet.controller;

import com.aiwalletplatform.commons.dto.ApiResponse;
import com.aiwalletplatform.commons.entity.Wallet;
import com.aiwalletplatform.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Wallet REST Controller
 */
@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Create new wallet
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Wallet>> createWallet(
            @RequestParam String userId,
            @RequestParam String walletName) {
        Wallet wallet = walletService.createWallet(userId, walletName);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    /**
     * Get wallet by ID
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse<Wallet>> getWallet(@PathVariable String walletId) {
        Wallet wallet = walletService.getWallet(walletId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    /**
     * Get wallet by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Wallet>> getWalletByUserId(@PathVariable String userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    /**
     * Credit wallet balance
     */
    @PostMapping("/{walletId}/credit")
    public ResponseEntity<ApiResponse<Wallet>> creditBalance(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.creditBalance(walletId, amount);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    /**
     * Debit wallet balance
     */
    @PostMapping("/{walletId}/debit")
    public ResponseEntity<ApiResponse<Wallet>> debitBalance(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.debitBalance(walletId, amount);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }

    /**
     * Update wallet status
     */
    @PutMapping("/{walletId}/status")
    public ResponseEntity<ApiResponse<Wallet>> updateStatus(
            @PathVariable String walletId,
            @RequestParam Wallet.WalletStatus status) {
        Wallet wallet = walletService.updateWalletStatus(walletId, status);
        return ResponseEntity.ok(ApiResponse.success(wallet));
    }
}
