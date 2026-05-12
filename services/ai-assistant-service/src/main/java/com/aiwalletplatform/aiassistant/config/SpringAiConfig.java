package com.aiwalletplatform.aiassistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI Configuration
 * 
 * Configures:
 * - ChatClient for LLM interactions
 * - Default model settings
 * - Temperature and token limits
 * - Response format preferences
 */
@Configuration
public class SpringAiConfig {

    /**
     * ChatClient Bean
     * 
     * The ChatClient is the primary interface for LLM interactions in Spring AI:
     * - Fluent API for building prompts
     * - Request/response streaming
     * - Tool calling support
     * - Memory management
     * - Error handling and retry logic
     * 
     * @param chatModel The underlying chat model (OpenAI/Anthropic)
     * @return Configured ChatClient
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    /**
     * ChatModel Configuration
     * 
     * This is automatically configured from application.yml:
     * - Model selection (gpt-4, gpt-3.5-turbo, claude-3, etc.)
     * - API key and endpoint
     * - Timeout and retry settings
     * - Temperature (creativity) setting
     * - Max tokens for responses
     */
    // ChatModel is auto-configured by Spring AI starter
}
