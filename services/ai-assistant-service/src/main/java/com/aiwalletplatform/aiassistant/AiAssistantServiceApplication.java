package com.aiwalletplatform.aiassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * AI Assistant Service Application
 * 
 * Demonstrates Spring AI capabilities:
 * - ChatClient for conversational AI
 * - Prompt templates for structured prompting
 * - Memory management for conversation context
 * - AI tool calling for structured outputs
 * - Token usage tracking and optimization
 * - Response validation and safety
 * 
 * Provides financial insights through natural language:
 * - Transaction explanations
 * - Spending analysis
 * - Budget recommendations
 * - Financial health assessment
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.aiwalletplatform.aiassistant", "com.aiwalletplatform.commons"})
public class AiAssistantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAssistantServiceApplication.class, args);
    }
}
