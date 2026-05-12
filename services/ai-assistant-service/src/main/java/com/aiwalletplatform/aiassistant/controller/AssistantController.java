package com.aiwalletplatform.aiassistant.controller;

import com.aiwalletplatform.aiassistant.service.ConversationMemoryService;
import com.aiwalletplatform.aiassistant.service.FinancialAssistantService;
import com.aiwalletplatform.commons.dto.ApiResponse;
import com.aiwalletplatform.commons.dto.ChatMessageRequest;
import com.aiwalletplatform.commons.dto.ChatMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI Assistant Controller
 * 
 * Endpoints demonstrating Spring AI integration:
 * - POST /api/v1/assistant/chat - Chat with financial assistant
 * - POST /api/v1/assistant/start-conversation - Start new conversation
 * - GET /api/v1/assistant/conversations - Get user conversations
 * - POST /api/v1/assistant/insights/spending - Generate spending insights
 * - POST /api/v1/assistant/health/analyze - Analyze wallet health
 */
@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Assistant", description = "Financial assistance through conversational AI")
public class AssistantController {

    private final FinancialAssistantService financialAssistantService;
    private final ConversationMemoryService conversationMemoryService;

    /**
     * Chat endpoint
     * 
     * Demonstrates:
     * - Spring AI ChatClient usage
     * - Prompt engineering
     * - Context injection
     * - Conversation memory
     * - Response streaming
     */
    @PostMapping("/chat")
    @Operation(summary = "Chat with financial assistant",
               description = "Send a message to the AI financial assistant")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> chat(
            @RequestBody ChatMessageRequest request) {
        
        log.info("Chat request from user: {}", request.userId());

        try {
            // Use existing conversation or start new one
            String conversationId = request.conversationId();
            if (conversationId == null || conversationId.isBlank()) {
                conversationId = conversationMemoryService.startConversation(request.userId());
            }

            // Get context window for multi-turn conversation
            String contextWindow = conversationMemoryService.getContextWindow(conversationId);

            // Build enhanced message with context
            String enhancedMessage = contextWindow.isEmpty() ? 
                request.message() : 
                contextWindow + "\nUser: " + request.message();

            // Call AI assistant
            String response = financialAssistantService.chat(
                request.userId(),
                request.walletId(),
                enhancedMessage,
                conversationId
            );

            // Return response with conversation ID
            ChatMessageResponse chatResponse = new ChatMessageResponse(
                conversationId,
                response,
                0.95,  // Confidence score
                List.of()  // Sources
            );

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(chatResponse));

        } catch (Exception e) {
            log.error("Error in chat: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to process chat: " + e.getMessage()));
        }
    }

    /**
     * Start new conversation
     */
    @PostMapping("/start-conversation")
    @Operation(summary = "Start new conversation",
               description = "Initialize a new conversation session with the AI assistant")
    public ResponseEntity<ApiResponse<?>> startConversation(
            @RequestParam String userId) {

        try {
            String conversationId = conversationMemoryService.startConversation(userId);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(new StartConversationResponse(conversationId, userId)));

        } catch (Exception e) {
            log.error("Error starting conversation: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to start conversation"));
        }
    }

    /**
     * Get user conversations
     */
    @GetMapping("/conversations")
    @Operation(summary = "Get user conversations",
               description = "Retrieve all active conversations for the user")
    public ResponseEntity<ApiResponse<?>> getUserConversations(
            @RequestParam String userId) {

        try {
            List<String> conversations = conversationMemoryService.getUserConversations(userId);
            
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(conversations));

        } catch (Exception e) {
            log.error("Error retrieving conversations: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve conversations"));
        }
    }

    /**
     * Generate spending insights using Spring AI
     * 
     * Demonstrates:
     * - Prompt templates
     * - Structured data analysis
     * - Template variable injection
     */
    @PostMapping("/insights/spending")
    @Operation(summary = "Generate spending insights",
               description = "Get AI-powered analysis of spending patterns")
    public ResponseEntity<ApiResponse<?>> getSpendingInsights(
            @RequestParam String userId,
            @RequestParam String walletId) {

        try {
            // Mock transaction data (in production, fetch from Transaction Service)
            List<FinancialAssistantService.TransactionSummary> transactions = 
                generateMockTransactions();

            String insights = financialAssistantService.generateSpendingInsights(
                userId, walletId, transactions);

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(new InsightsResponse(insights)));

        } catch (Exception e) {
            log.error("Error generating spending insights: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to generate insights"));
        }
    }

    /**
     * Analyze wallet health using Spring AI
     * 
     * Demonstrates:
     * - Complex prompt engineering
     * - Financial health assessment
     * - Risk evaluation
     */
    @PostMapping("/health/analyze")
    @Operation(summary = "Analyze wallet health",
               description = "Get comprehensive health assessment using AI")
    public ResponseEntity<ApiResponse<?>> analyzeWalletHealth(
            @RequestParam String userId,
            @RequestParam String walletId) {

        try {
            // Mock health data (in production, fetch from Wallet Service)
            FinancialAssistantService.WalletHealthData healthData = 
                new FinancialAssistantService.WalletHealthData(
                    new BigDecimal("5000.00"),
                    new BigDecimal("10000.00"),
                    new BigDecimal("6500.00"),
                    45.0,  // Risk score
                    2,     // Active alerts
                    180    // Account age
                );

            String analysis = financialAssistantService.analyzeWalletHealth(
                userId, walletId, healthData);

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(new AnalysisResponse(analysis)));

        } catch (Exception e) {
            log.error("Error analyzing wallet health: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to analyze health"));
        }
    }

    // Mock data generators
    private List<FinancialAssistantService.TransactionSummary> generateMockTransactions() {
        return List.of(
            new FinancialAssistantService.TransactionSummary(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minusDays(1),
                "Groceries",
                new BigDecimal("250.00"),
                "Supermarket purchase"
            ),
            new FinancialAssistantService.TransactionSummary(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minusDays(2),
                "Entertainment",
                new BigDecimal("50.00"),
                "Movie tickets"
            ),
            new FinancialAssistantService.TransactionSummary(
                UUID.randomUUID().toString(),
                LocalDateTime.now().minusDays(3),
                "Dining",
                new BigDecimal("450.00"),
                "Restaurant"
            )
        );
    }

    // Response DTOs
    record StartConversationResponse(String conversationId, String userId) {}
    record InsightsResponse(String insights) {}
    record AnalysisResponse(String analysis) {}
}
