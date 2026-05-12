package com.aiwalletplatform.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Wallet Service - Core Wallet Management
 * 
 * Manages wallet lifecycle, balance operations, and wallet-related business logic.
 * Implements CQRS pattern for read/write optimization.
 * 
 * Port: 8083
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WalletServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletServiceApplication.class, args);
    }
}
