package com.aiwalletplatform.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Transaction Service - Immutable Transaction Ledger
 * 
 * Records and maintains immutable transaction history.
 * Uses event sourcing and integrates with Elasticsearch for analytics.
 * 
 * Port: 8084
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
