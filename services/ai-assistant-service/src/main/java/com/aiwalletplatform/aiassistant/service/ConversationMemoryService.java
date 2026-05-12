package com.aiwalletplatform.aiassistant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Conversation Memory Service
 * 
 * Manages chat history and context in Redis:
 * - Stores conversation history for context window
 * - Enables multi-turn conversations
 * - Maintains user conversation sessions
 * - Automatic expiration of old conversations
 * 
 * Redis Key Structure:
 * - conversation:{conversationId} -> List of messages
 * - conversation:{conversationId}:metadata -> Metadata
 * - user:{userId}:conversations -> List of conversation IDs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationMemoryService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CONVERSATION_TTL = 24; // 24 hours
    private static final int MAX_MESSAGES_IN_CONTEXT = 20;

    /**
     * Store message in conversation
     */
    public void storeMessage(String conversationId, String userId, String userMessage, 
                             String aiResponse, long latencyMs) {
        try {
            ConversationMessage message = new ConversationMessage(
                UUID.randomUUID().toString(),
                conversationId,
                userId,
                userMessage,
                aiResponse,
                LocalDateTime.now(),
                latencyMs
            );

            // Store message in Redis
            String messageKey = "conversation:" + conversationId + ":messages";
            redisTemplate.opsForList().rightPush(messageKey, message);

            // Trim to keep only last MAX_MESSAGES_IN_CONTEXT
            redisTemplate.opsForList().trim(messageKey, 
                -MAX_MESSAGES_IN_CONTEXT, -1);

            // Set TTL
            redisTemplate.expire(messageKey, CONVERSATION_TTL, TimeUnit.HOURS);

            // Update metadata
            updateConversationMetadata(conversationId, userId);

            log.debug("Message stored in conversation: {}", conversationId);

        } catch (Exception e) {
            log.error("Error storing message: {}", e.getMessage(), e);
        }
    }

    /**
     * Get conversation history for context
     */
    public List<ConversationMessage> getConversationHistory(String conversationId) {
        try {
            String messageKey = "conversation:" + conversationId + ":messages";
            List<Object> messages = redisTemplate.opsForList()
                .range(messageKey, 0, -1);

            return messages == null ? new ArrayList<>() : messages.stream()
                .map(m -> (ConversationMessage) m)
                .toList();

        } catch (Exception e) {
            log.error("Error retrieving conversation history: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get context window (last 10 messages for LLM)
     */
    public String getContextWindow(String conversationId) {
        List<ConversationMessage> messages = getConversationHistory(conversationId);
        
        if (messages.isEmpty()) {
            return "";
        }

        // Get last 10 messages
        List<ConversationMessage> contextMessages = messages.stream()
            .skip(Math.max(0, messages.size() - 10))
            .toList();

        StringBuilder context = new StringBuilder("Previous conversation:\n");
        for (ConversationMessage msg : contextMessages) {
            context.append("User: ").append(msg.userMessage()).append("\n");
            context.append("Assistant: ").append(msg.aiResponse()).append("\n\n");
        }

        return context.toString();
    }

    /**
     * Start new conversation
     */
    public String startConversation(String userId) {
        String conversationId = UUID.randomUUID().toString();

        try {
            // Initialize conversation metadata
            ConversationMetadata metadata = new ConversationMetadata(
                conversationId,
                userId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0
            );

            String metadataKey = "conversation:" + conversationId + ":metadata";
            redisTemplate.opsForValue().set(metadataKey, metadata, CONVERSATION_TTL, TimeUnit.HOURS);

            // Add to user's conversation list
            String userConversationsKey = "user:" + userId + ":conversations";
            redisTemplate.opsForSet().add(userConversationsKey, conversationId);
            redisTemplate.expire(userConversationsKey, CONVERSATION_TTL, TimeUnit.HOURS);

            log.info("New conversation started: {} for user: {}", conversationId, userId);

            return conversationId;

        } catch (Exception e) {
            log.error("Error starting conversation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start conversation", e);
        }
    }

    /**
     * Get all conversations for user
     */
    public List<String> getUserConversations(String userId) {
        try {
            String userConversationsKey = "user:" + userId + ":conversations";
            Set<Object> conversations = redisTemplate.opsForSet()
                .members(userConversationsKey);

            return conversations == null ? new ArrayList<>() : conversations.stream()
                .map(String::valueOf)
                .toList();

        } catch (Exception e) {
            log.error("Error retrieving user conversations: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * End conversation
     */
    public void endConversation(String conversationId) {
        try {
            // Just let Redis TTL handle cleanup
            log.debug("Conversation ended: {}", conversationId);

        } catch (Exception e) {
            log.error("Error ending conversation: {}", e.getMessage(), e);
        }
    }

    private void updateConversationMetadata(String conversationId, String userId) {
        try {
            String metadataKey = "conversation:" + conversationId + ":metadata";
            ConversationMetadata metadata = (ConversationMetadata) 
                redisTemplate.opsForValue().get(metadataKey);

            if (metadata != null) {
                ConversationMetadata updated = new ConversationMetadata(
                    metadata.conversationId(),
                    metadata.userId(),
                    metadata.startedAt(),
                    LocalDateTime.now(),
                    metadata.messageCount() + 1
                );
                redisTemplate.opsForValue().set(metadataKey, updated, 
                    CONVERSATION_TTL, TimeUnit.HOURS);
            }

        } catch (Exception e) {
            log.error("Error updating conversation metadata: {}", e.getMessage(), e);
        }
    }

    // DTOs for Redis storage
    public record ConversationMessage(
        String id,
        String conversationId,
        String userId,
        String userMessage,
        String aiResponse,
        LocalDateTime timestamp,
        long latencyMs
    ) implements Serializable {}

    public record ConversationMetadata(
        String conversationId,
        String userId,
        LocalDateTime startedAt,
        LocalDateTime lastUpdatedAt,
        int messageCount
    ) implements Serializable {}
}
