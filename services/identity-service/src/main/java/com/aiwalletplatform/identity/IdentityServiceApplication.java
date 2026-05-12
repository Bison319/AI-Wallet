package com.aiwalletplatform.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Identity Service Application
 * 
 * Responsible for:
 * - User authentication (login, register)
 * - JWT token generation and validation
 * - Refresh token management
 * - Role-based access control (RBAC)
 * - OAuth2 integration with Keycloak
 * - Token blacklisting/revocation
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.aiwalletplatform.identity", "com.aiwalletplatform.commons"})
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
