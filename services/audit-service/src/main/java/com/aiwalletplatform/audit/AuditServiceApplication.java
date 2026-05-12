package com.aiwalletplatform.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Audit Service - Immutable Audit Trail
 * 
 * Maintains immutable audit logs for compliance and regulatory requirements.
 * Records all system actions and state changes.
 * 
 * Port: 8091
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AuditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }
}
