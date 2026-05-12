package com.aiwalletplatform.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Payment Service - Payment Order and Settlement Management
 * 
 * Orchestrates payment orders and settlement operations using Saga pattern.
 * Coordinates with Wallet, Transaction, and Notification services.
 * 
 * Port: 8085
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
