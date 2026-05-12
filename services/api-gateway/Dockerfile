# Multi-stage Dockerfile for API Gateway
# Demonstrates production-grade containerization with Java 21

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy parent pom
COPY pom.xml .
COPY commons commons/
COPY services/api-gateway services/api-gateway/

# Build application
RUN mvn clean package -DskipTests -pl services/api-gateway -am

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Security: Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy built artifact from builder
COPY --from=builder /build/services/api-gateway/target/api-gateway-*.jar api-gateway.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -cp app:app/* \
    -Dloader.main=org.springframework.boot.loader.JarLauncher \
    org.springframework.boot.loader.JarLauncher \
    GET http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM optimization flags
ENV JAVA_OPTS="-XX:+UseG1GC \
                -XX:MaxGCPauseMillis=200 \
                -XX:InitiatingHeapOccupancyPercent=35 \
                -XX:+ParallelRefProcEnabled \
                -Djava.util.concurrent.ForkJoinPool.common.parallelism=16"

# Run application
ENTRYPOINT ["java", \
            "-Xms512m", \
            "-Xmx1024m", \
            "-XX:+UnlockExperimentalVMOptions", \
            "-XX:+UseZGC", \
            "-XX:-OmitStackTraceInFastThrow", \
            "-Dspring.profiles.active=${ENVIRONMENT:=prod}", \
            "-jar", \
            "api-gateway.jar"]
