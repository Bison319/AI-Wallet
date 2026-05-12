package com.aiwalletplatform.apigateway.filter;

import com.aiwalletplatform.commons.util.CorrelationIdContext;
import com.aiwalletplatform.commons.util.RequestContextHolder;
import com.aiwalletplatform.commons.util.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * JWT Authentication Filter for API Gateway
 * 
 * Validates JWT tokens, extracts user claims, and propagates
 * authentication context to downstream services
 */
@Component
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Value("${jwt.secret:my-super-secret-key-for-production-use-very-long-string-here}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Set correlation ID for distributed tracing
            String correlationId = exchange.getRequest().getHeaders()
                .getFirst("X-Correlation-ID");
            if (correlationId == null) {
                correlationId = UUID.randomUUID().toString();
            }
            CorrelationIdContext.setCorrelationId(correlationId);

            // Skip JWT validation for public endpoints
            if (isPublicEndpoint(exchange.getRequest().getURI().getPath())) {
                exchange.getRequest().mutate()
                    .header("X-Correlation-ID", correlationId)
                    .build();
                return chain.filter(exchange);
            }

            // Extract and validate JWT token
            String token = extractToken(exchange);
            if (token == null) {
                log.warn("Missing JWT token in Authorization header");
                return onError(exchange, "Missing JWT token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Validate and parse JWT
                Claims claims = validateToken(token);
                
                // Extract user information
                String userId = claims.getSubject();
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);

                // Create request context
                RequestContext context = new RequestContext(userId, correlationId, 
                    exchange.getRequest().getRemoteAddress() != null ? 
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : 
                    "UNKNOWN");
                RequestContextHolder.setContext(context);

                // Add headers for downstream services
                exchange = exchange.mutate()
                    .request(r -> r
                        .header("X-User-Id", userId)
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .header("X-Correlation-ID", correlationId)
                        .header("X-Request-Timestamp", String.valueOf(System.currentTimeMillis())))
                    .build();

                log.debug("JWT validation successful for user: {}", userId);
                return chain.filter(exchange);

            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Check if endpoint is public (no authentication required)
     */
    private boolean isPublicEndpoint(String path) {
        return path.contains("/auth/login") ||
               path.contains("/auth/register") ||
               path.contains("/auth/refresh") ||
               path.contains("/health") ||
               path.contains("/actuator");
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Validate JWT token and return claims
     */
    private Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Handle authentication error
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format("{\"error\":\"%s\",\"status\":%d}", message, status.value());
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    public static class Config {
        // Config properties if needed
    }
}
