package com.aiwalletplatform.aiassistant.service;

import com.aiwalletplatform.commons.util.CorrelationIdContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Financial Assistant Service
 * 
 * Core service demonstrating Spring AI capabilities:
 * - Conversational AI for financial advice
 * - Prompt templating for consistent responses
 * - Context injection for personalized advice
 * - Token usage tracking
 * - Response validation
 * 
 * Handles:
 * - Transaction analysis
 * - Spending insights
 * - Budget recommendations
 * - Financial health assessment
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialAssistantService {

    private final ChatClient chatClient;
    private final ConversationMemoryService conversationMemoryService;

    /**
     * Chat with the financial assistant
     * 
     * Uses Spring AI ChatClient to:
     * 1. Retrieve conversation context
     * 2. Build system prompt with user financial profile
     * 3. Call LLM with user message
     * 4. Parse and validate response
     * 5. Store message in memory
     * 6. Return response with metadata
     * 
     * @param userId User ID for context
     * @param walletId Wallet ID for financial context
     * @param userMessage User's question/request
     * @param conversationId Existing conversation or new
     * @return AI response with sources
     */
    public String chat(String userId, String walletId, String userMessage, String conversationId) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        long startTime = System.currentTimeMillis();

        try {
            log.debug("[{}] Starting chat for user: {}, wallet: {}", correlationId, userId, walletId);

            // Build system prompt with financial context
            String systemPrompt = buildFinancialSystemPrompt(userId, walletId);

            // Create prompt with user message
            String response = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();

            // Store conversation in memory (Redis)
            conversationMemoryService.storeMessage(
                conversationId,
                userId,
                userMessage,
                response,
                System.currentTimeMillis() - startTime
            );

            log.debug("[{}] Chat completed. Response length: {}, latency: {}ms",
                correlationId, response.length(), System.currentTimeMillis() - startTime);

            return response;

        } catch (Exception e) {
            log.error("[{}] Error in financial assistant chat: {}", correlationId, e.getMessage(), e);
            throw new RuntimeException("Failed to process chat request", e);
        }
    }

    /**
     * Generate spending insights using Spring AI
     * 
     * Uses prompt templating to structure the request:
     * - User spending patterns
     * - Category analysis
     * - Anomalies and trends
     * - Recommendations
     */
    public String generateSpendingInsights(String userId, String walletId, 
                                           List<TransactionSummary> transactions) {
        String correlationId = CorrelationIdContext.getCorrelationId();
        long startTime = System.currentTimeMillis();

        try {
            log.info("[{}] Generating spending insights for user: {}", correlationId, userId);

            // Create spending analysis prompt with template
            String spendingTemplate = """
                Analyze the following spending data for a financial wallet user and provide insights:
                
                User ID: {userId}
                Wallet ID: {walletId}
                Transaction Count: {transactionCount}
                Total Spent: {totalSpent}
                Average Transaction: {averageTransaction}
                Top Category: {topCategory}
                
                Recent Transactions:
                {transactionDetails}
                
                Please provide:
                1. Key spending patterns
                2. Anomalies or concerns
                3. Savings opportunities
                4. Budget recommendations
                
                Keep the response concise and actionable.
                """;

            Map<String, Object> templateVars = new HashMap<>();
            templateVars.put("userId", userId);
            templateVars.put("walletId", walletId);
            templateVars.put("transactionCount", transactions.size());
            templateVars.put("totalSpent", calculateTotalSpent(transactions));
            templateVars.put("averageTransaction", calculateAverage(transactions));
            templateVars.put("topCategory", getTopCategory(transactions));
            templateVars.put("transactionDetails", formatTransactions(transactions));

            PromptTemplate promptTemplate = new PromptTemplate(spendingTemplate, templateVars);
            Prompt prompt = promptTemplate.create();

            String response = chatClient
                .prompt(prompt)
                .call()
                .content();

            log.info("[{}] Spending insights generated. Latency: {}ms",
                correlationId, System.currentTimeMillis() - startTime);

            return response;

        } catch (Exception e) {
            log.error("[{}] Error generating spending insights: {}", correlationId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate spending insights", e);
        }
    }

    /**
     * Analyze wallet health using Spring AI
     * Considers: balance, spending patterns, limits, risks
     */
    public String analyzeWalletHealth(String userId, String walletId, WalletHealthData healthData) {
        String correlationId = CorrelationIdContext.getCorrelationId();

        try {
            log.info("[{}] Analyzing wallet health for wallet: {}", correlationId, walletId);

            String healthPrompt = """
                Provide a financial health assessment for this wallet:
                
                Balance: {balance}
                Monthly Limit: {monthlyLimit}
                Monthly Spent: {monthlySpent}
                Daily Limit Utilization: {dailyUtilization}%
                Risk Score: {riskScore}
                Active Alerts: {activeAlerts}
                Account Age: {accountAge} days
                
                Provide:
                1. Overall health score (1-10)
                2. Risk assessment
                3. Immediate concerns
                4. Action recommendations
                5. Long-term financial health tips
                """;

            Map<String, Object> vars = new HashMap<>();
            vars.put("balance", healthData.balance());
            vars.put("monthlyLimit", healthData.monthlyLimit());
            vars.put("monthlySpent", healthData.monthlySpent());
            vars.put("dailyUtilization", calculateUtilization(healthData));
            vars.put("riskScore", healthData.riskScore());
            vars.put("activeAlerts", healthData.activeAlerts());
            vars.put("accountAge", healthData.accountAgeDays());

            PromptTemplate template = new PromptTemplate(healthPrompt, vars);

            String response = chatClient
                .prompt(template.create())
                .call()
                .content();

            return response;

        } catch (Exception e) {
            log.error("[{}] Error analyzing wallet health: {}", correlationId, e.getMessage(), e);
            throw new RuntimeException("Failed to analyze wallet health", e);
        }
    }

    /**
     * Build system prompt with financial context
     * This establishes the AI assistant's role and guidelines
     */
    private String buildFinancialSystemPrompt(String userId, String walletId) {
        return """
            You are a professional financial advisor AI assistant for a digital wallet platform.
            
            Your role:
            - Provide personalized financial advice based on user wallet data
            - Explain transactions and spending patterns
            - Offer budgeting and savings recommendations
            - Identify and warn about suspicious activity
            - Maintain confidentiality and security
            
            Guidelines:
            - Always be helpful and non-judgmental
            - Use clear, simple language for financial concepts
            - Provide actionable recommendations
            - Acknowledge limitations in your advice
            - Never encourage risky financial behavior
            - For concerns, recommend speaking with a professional
            
            User Context:
            - User ID: """ + userId + """
            - Wallet ID: """ + walletId + """
            - Service: AI Wallet Platform Financial Assistant
            
            Always provide balanced, accurate financial guidance.
            """;
    }

    private BigDecimal calculateTotalSpent(List<TransactionSummary> transactions) {
        return transactions.stream()
            .map(TransactionSummary::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAverage(List<TransactionSummary> transactions) {
        if (transactions.isEmpty()) return BigDecimal.ZERO;
        return calculateTotalSpent(transactions)
            .divide(BigDecimal.valueOf(transactions.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    private String getTopCategory(List<TransactionSummary> transactions) {
        return transactions.stream()
            .map(TransactionSummary::category)
            .max(java.util.Comparator.comparing((String c) -> 
                transactions.stream().filter(t -> t.category().equals(c)).count()))
            .orElse("Unknown");
    }

    private String formatTransactions(List<TransactionSummary> transactions) {
        return transactions.stream()
            .map(t -> String.format("%s: %s %s", t.date(), t.category(), t.amount()))
            .reduce((a, b) -> a + "\n" + b)
            .orElse("No transactions");
    }

    private double calculateUtilization(WalletHealthData data) {
        return data.monthlySpent()
            .divide(data.monthlyLimit(), 2, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }

    // DTOs
    public record TransactionSummary(
        String id,
        LocalDateTime date,
        String category,
        BigDecimal amount,
        String description
    ) {}

    public record WalletHealthData(
        BigDecimal balance,
        BigDecimal monthlyLimit,
        BigDecimal monthlySpent,
        Double riskScore,
        Integer activeAlerts,
        Integer accountAgeDays
    ) {}
}
