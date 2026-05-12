package com.aiwalletplatform.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Config Server - Spring Cloud Config Server
 * 
 * Provides centralized configuration management for all microservices.
 * Services can fetch their configuration from this server instead of
 * managing local application.yml files.
 * 
 * Access: http://localhost:8888
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
