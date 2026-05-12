package com.aiwalletplatform.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Notification Service - Event-Driven Messaging
 * 
 * Listens to Kafka events and sends notifications via email, SMS, and push.
 * Manages notification templates and delivery tracking.
 * 
 * Port: 8088
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
