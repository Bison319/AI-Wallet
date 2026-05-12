package com.aiwalletplatform.frauddetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Fraud Detection Service - AI-Powered Risk Analysis
 * 
 * Uses Spring AI to detect suspicious activity and anomalies in transactions.
 * Analyzes patterns and generates fraud alerts in real-time.
 * 
 * Port: 8086
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FraudDetectionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionServiceApplication.class, args);
    }
}
