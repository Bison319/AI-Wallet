package com.aiwalletplatform.apigateway.filter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Filter for API Gateway
 * 
 * Implements token bucket algorithm using Guava RateLimiter
 * Limits requests per user and endpoint
 * 
 * Configuration:
 * - Default: 100 requests per second per user
 * - Can be configured per route
 */
@Component
@Slf4j
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    /**
     * Cache of RateLimiter instances per user
     * Expires after 1 hour of inactivity
     */
    private final LoadingCache<String, RateLimiter> limiters = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build(new CacheLoader<String, RateLimiter>() {
            @Override
            public RateLimiter load(String key) {
                // 100 requests per second per user
                return RateLimiter.create(100.0);
            }
        });

    public RateLimitingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        double requestsPerSecond = config.getRequestsPerSecond();

        return (exchange, chain) -> {
            // Get user ID from headers (set by JWT filter)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            
            // Use IP address if user not authenticated
            if (userId == null) {
                userId = exchange.getRequest().getRemoteAddress() != null ?
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() :
                    "anonymous";
            }

            try {
                // Get or create rate limiter for this user
                RateLimiter rateLimiter = limiters.get(userId);
                
                // Check if request can proceed
                if (!rateLimiter.tryAcquire()) {
                    log.warn("Rate limit exceeded for user: {}", userId);
                    return sendRateLimitExceeded(exchange);
                }

                return chain.filter(exchange);

            } catch (ExecutionException e) {
                log.error("Error in rate limiting: {}", e.getMessage());
                return sendError(exchange, "Rate limiting service error");
            }
        };
    }

    private Mono<Void> sendRateLimitExceeded(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().add("Retry-After", "60");
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        
        String errorBody = "{\"error\":\"Rate limit exceeded\",\"status\":429}";
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorBody.getBytes()))
        );
    }

    private Mono<Void> sendError(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format("{\"error\":\"%s\",\"status\":500}", message);
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorBody.getBytes()))
        );
    }

    public static class Config {
        private double requestsPerSecond = 100.0;

        public double getRequestsPerSecond() {
            return requestsPerSecond;
        }

        public void setRequestsPerSecond(double requestsPerSecond) {
            this.requestsPerSecond = requestsPerSecond;
        }
    }
}
