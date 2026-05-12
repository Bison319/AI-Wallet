package com.aiwalletplatform.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service - Profile and KYC Management
 * 
 * Manages user profiles, KYC (Know Your Customer) data, and user preferences.
 * Handles user information that doesn't fit into identity or wallet services.
 * 
 * Port: 8082
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
