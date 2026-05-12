package com.aiwalletplatform.investigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Investigation Service - RAG-Based Compliance Investigation
 * 
 * Provides Retrieval-Augmented Generation (RAG) for semantic search and investigation.
 * Uses PostgreSQL with pgvector for embeddings and Spring AI for RAG queries.
 * Supports compliance audits and forensic analysis.
 * 
 * Port: 8087
 */
@SpringBootApplication
@EnableDiscoveryClient
public class InvestigationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestigationServiceApplication.class, args);
    }
}
