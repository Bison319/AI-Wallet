package com.aiwalletplatform.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Analytics Service - OLAP and Business Intelligence
 * 
 * Provides analytics and business intelligence using Elasticsearch.
 * Aggregates metrics and generates reports for platform insights.
 * 
 * Port: 8090
 */
@SpringBootApplication
@EnableDiscoveryClient
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}
