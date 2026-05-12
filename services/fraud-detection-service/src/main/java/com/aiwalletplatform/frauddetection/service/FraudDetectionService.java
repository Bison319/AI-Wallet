package com.aiwalletplatform.frauddetection.service;

import com.aiwalletplatform.commons.entity.FraudAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fraud Detection Service using Spring AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final ChatClient chatClient;

    /**
     * Analyze transaction for fraud using AI
     */
    public FraudAlert analyzeTransaction(String walletId, String userId, 
                                          BigDecimal amount, String description) {
        log.info("Analyzing transaction for fraud - wallet: {}, amount: {}", walletId, amount);

        // Use Spring AI to analyze fraud risk
        String prompt = String.format("""
                Analyze this financial transaction for fraud risk:
                - Wallet ID: %s
                - User ID: %s
                - Amount: %s
                - Description: %s
                
                Provide risk assessment (LOW, MEDIUM, HIGH) and brief explanation.
                """, walletId, userId, amount, description);

        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        FraudAlert fraudAlert = new FraudAlert();
        fraudAlert.setId(UUID.randomUUID().toString());
        fraudAlert.setWalletId(walletId);
        fraudAlert.setUserId(userId);
        fraudAlert.setTransactionAmount(amount);
        fraudAlert.setAnalysisResult(aiResponse);
        fraudAlert.setStatus(FraudAlert.FraudAlertStatus.PENDING);
        fraudAlert.setCreatedAt(LocalDateTime.now());

        log.info("Fraud analysis completed for transaction in wallet: {}", walletId);
        return fraudAlert;
    }

    /**
     * Get risk score using behavioral analysis
     */
    public Double calculateRiskScore(String userId, BigDecimal amount, String merchantCategory) {
        String prompt = String.format("""
                Calculate fraud risk score (0-100) for this user activity:
                - User ID: %s
                - Transaction Amount: %s
                - Merchant Category: %s
                
                Return only a numeric score between 0-100.
                """, userId, amount, merchantCategory);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        try {
            return Double.parseDouble(response.trim());
        } catch (NumberFormatException e) {
            log.warn("Could not parse risk score response: {}", response);
            return 50.0; // Default medium risk
        }
    }
}
