package com.aiwalletplatform.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * API Gateway Application - Central entry point for all microservices
 * 
 * Features:
 * - Request routing to microservices
 * - JWT authentication/authorization
 * - Rate limiting
 * - Correlation ID propagation
 * - Distributed tracing
 * - Request/response logging
 * - CircuitBreaker protection
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    /**
     * Configure microservice routes
     * Each route includes predicates, filters, and targets specific services
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Identity Service routes
            .route("identity-service", r -> r
                .path("/api/v1/auth/**")
                .uri("lb://identity-service"))
            
            // User Service routes
            .route("user-service", r -> r
                .path("/api/v1/users/**")
                .uri("lb://user-service"))
            
            // Wallet Service routes
            .route("wallet-service", r -> r
                .path("/api/v1/wallets/**")
                .uri("lb://wallet-service"))
            
            // Transaction Service routes
            .route("transaction-service", r -> r
                .path("/api/v1/transactions/**")
                .uri("lb://transaction-service"))
            
            // Payment Service routes
            .route("payment-service", r -> r
                .path("/api/v1/payments/**")
                .uri("lb://payment-service"))
            
            // Fraud Detection Service routes
            .route("fraud-detection-service", r -> r
                .path("/api/v1/fraud/**")
                .uri("lb://fraud-detection-service"))
            
            // AI Assistant Service routes
            .route("ai-assistant-service", r -> r
                .path("/api/v1/assistant/**")
                .uri("lb://ai-assistant-service"))
            
            // Investigation Service routes
            .route("investigation-service", r -> r
                .path("/api/v1/investigations/**")
                .uri("lb://investigation-service"))
            
            // Analytics Service routes
            .route("analytics-service", r -> r
                .path("/api/v1/analytics/**")
                .uri("lb://analytics-service"))
            
            .build();
    }
}
