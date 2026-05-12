# Implementation Guide - AI-Native Digital Wallet Platform

## Complete Setup and Deployment Instructions

---

## Part 1: Local Development Setup

### 1.1 Prerequisites

```bash
# System Requirements
- Java 21 JDK
- Maven 3.9+
- Docker & Docker Compose
- Git
- Node.js 18+ (for React frontend)
```

### 1.2 Clone and Navigate

```bash
git clone https://github.com/yourusername/ai-wallet-platform.git
cd ai-wallet-platform
```

### 1.3 Start Infrastructure (Docker Compose)

```bash
# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps

# View logs
docker-compose logs -f
```

**Services Started**:
- PostgreSQL (5432)
- Redis (6379)
- Kafka + Zookeeper (9092)
- Milvus Vector DB (19530)
- Elasticsearch (9200)
- Prometheus (9090)
- Grafana (3000) - admin/admin
- Jaeger UI (16686)
- PgAdmin (5050)

### 1.4 Build the Project

```bash
# Full build with tests
mvn clean install -DskipTests

# or with tests (requires services running)
mvn clean install

# Build specific service
cd services/api-gateway
mvn clean package
```

---

## Part 2: Running Services Locally

### 2.1 Service Startup Order

Services should be started in this order:

#### 1. **Discovery Server** (Spring Cloud Eureka)
```bash
cd services/discovery-server
mvn spring-boot:run
# Runs on http://localhost:8761
```

#### 2. **Config Server** (Spring Cloud Config)
```bash
cd services/config-server
mvn spring-boot:run
# Runs on http://localhost:8888
```

#### 3. **API Gateway**
```bash
cd services/api-gateway
mvn spring-boot:run
# Runs on http://localhost:8080
```

#### 4. **Identity Service**
```bash
cd services/identity-service
mvn spring-boot:run
# Runs on http://localhost:8081
```

#### 5. **AI Assistant Service** (Core AI Feature)
```bash
cd services/ai-assistant-service
mvn spring-boot:run
# Runs on http://localhost:8089
```

#### 6. **Other Services** (As needed)
```bash
# User Service
cd services/user-service && mvn spring-boot:run

# Wallet Service
cd services/wallet-service && mvn spring-boot:run

# Transaction Service
cd services/transaction-service && mvn spring-boot:run

# Fraud Detection Service
cd services/fraud-detection-service && mvn spring-boot:run

# Investigation Service
cd services/investigation-service && mvn spring-boot:run
```

---

## Part 3: Testing the API

### 3.1 User Registration

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210"
  }'

# Response:
{
  "success": true,
  "data": {
    "id": "user-uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210",
    "kycCompleted": false,
    "accountStatus": "ACTIVE"
  },
  "timestamp": "2024-05-12T10:30:00"
}
```

### 3.2 User Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123!"
  }'

# Response:
{
  "success": true,
  "data": {
    "userId": "user-uuid",
    "email": "user@example.com",
    "role": "USER",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "tokenType": "Bearer"
  },
  "timestamp": "2024-05-12T10:30:00"
}
```

### 3.3 Chat with AI Assistant

```bash
# Start a new conversation
curl -X POST http://localhost:8080/api/v1/assistant/start-conversation \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId": "user-uuid"}'

# Get conversation ID from response

# Send a message
curl -X POST http://localhost:8080/api/v1/assistant/chat \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-uuid",
    "walletId": "wallet-uuid",
    "conversationId": "conv-uuid",
    "message": "Where did I spend the most this month?"
  }'

# Response from Spring AI:
{
  "success": true,
  "data": {
    "conversationId": "conv-uuid",
    "response": "Based on your transaction history, you spent the most on groceries, totaling $450 this month. This represents 35% of your total spending. Your second largest expense category was dining at $300...",
    "confidence": 0.95,
    "sources": []
  },
  "timestamp": "2024-05-12T10:30:00"
}
```

### 3.4 Generate Spending Insights

```bash
curl -X POST http://localhost:8080/api/v1/assistant/insights/spending \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId": "user-uuid", "walletId": "wallet-uuid"}'

# Response: AI-generated spending analysis
```

---

## Part 4: Spring AI Configuration

### 4.1 OpenAI Setup

Set your OpenAI API key:

```bash
# Option 1: Environment variable
export OPENAI_API_KEY="sk-your-api-key-here"

# Option 2: application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        model: gpt-4
```

### 4.2 Anthropic Claude Setup

```bash
# application.yml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        model: claude-3-opus
```

### 4.3 Local Ollama Setup (Open Source)

```bash
# Install Ollama: https://ollama.ai
ollama run mistral

# application.yml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: mistral
```

---

## Part 5: Database Management

### 5.1 Access PostgreSQL

```bash
# Via Docker
docker exec -it ai-wallet-postgres psql -U postgres

# View all databases
\l

# Connect to identity database
\c ai_wallet_identity

# View tables
\dt

# Exit
\q
```

### 5.2 PgAdmin Access

Open: http://localhost:5050
- Email: admin@example.com
- Password: admin

Add server:
- Hostname: postgres
- Port: 5432
- Database: postgres
- Username: postgres
- Password: postgres

### 5.3 Database Migrations

```bash
# Using Flyway (if configured)
mvn flyway:migrate

# Verify schema
docker exec -it ai-wallet-postgres psql -U postgres -d ai_wallet_identity -c "\dt"
```

---

## Part 6: Monitoring & Observability

### 6.1 Prometheus Metrics

Access: http://localhost:9090

**Query Examples**:
```promql
# API Gateway request rate
rate(http_requests_total[5m])

# Latency percentile
histogram_quantile(0.99, http_request_duration_seconds)

# Kafka consumer lag
kafka_consumer_lag_sum

# JVM memory usage
jvm_memory_used_bytes
```

### 6.2 Grafana Dashboards

Access: http://localhost:3000
- Username: admin
- Password: admin

**Default Dashboards**:
- Spring Boot Application
- Kafka Metrics
- Database Performance
- AI Assistant Performance

### 6.3 Jaeger Distributed Tracing

Access: http://localhost:16686

**Features**:
- Trace microservice calls
- View latency by service
- Identify bottlenecks
- Analyze error chains

### 6.4 Elasticsearch & Kibana (Optional)

```bash
# Install Kibana
docker run -p 5601:5601 -e ELASTICSEARCH_HOSTS=http://elasticsearch:9200 docker.elastic.co/kibana/kibana:8.10.0
```

---

## Part 7: Event Streaming with Kafka

### 7.1 Create Topics

```bash
# Connect to Kafka container
docker exec -it ai-wallet-kafka bash

# Create topics
kafka-topics --create --topic transactions.created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic fraud.alerts --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic wallets.created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# List topics
kafka-topics --list --bootstrap-server localhost:9092

# View topic details
kafka-topics --describe --topic transactions.created --bootstrap-server localhost:9092
```

### 7.2 Monitor Messages

```bash
# Consume messages from topic
kafka-console-consumer --topic transactions.created --bootstrap-server localhost:9092 --from-beginning
```

---

## Part 8: AI Integration Testing

### 8.1 Test Spring AI Prompt Templates

```java
// Test prompt templating
@Test
void testPromptTemplate() {
    String template = "Analyze this spending: {amount} on {category}";
    Map<String, Object> vars = Map.of("amount", 250, "category", "groceries");
    
    PromptTemplate pt = new PromptTemplate(template, vars);
    Prompt prompt = pt.create();
    
    String response = chatClient.prompt(prompt).call().content();
    assertNotNull(response);
}
```

### 8.2 Test Memory Management

```java
@Test
void testConversationMemory() {
    String conversationId = conversationMemoryService.startConversation("user-123");
    
    conversationMemoryService.storeMessage(
        conversationId, "user-123", "Hello", "Hi there!", 100);
    
    List<ConversationMessage> history = 
        conversationMemoryService.getConversationHistory(conversationId);
    
    assertEquals(1, history.size());
}
```

---

## Part 9: Deployment Checklist

### 9.1 Pre-Deployment

- [ ] All services built and tested
- [ ] Environment variables configured
- [ ] Database schema initialized
- [ ] SSL/TLS certificates generated
- [ ] Redis cluster configured (if applicable)
- [ ] Kafka replication configured
- [ ] Monitoring dashboards created
- [ ] Logging aggregation set up
- [ ] Backup strategy defined

### 9.2 Deployment Steps

1. **Build Docker Images**
```bash
mvn clean package -DskipTests
docker build -f services/api-gateway/Dockerfile -t aiwalletplatform/api-gateway:latest .
docker build -f services/identity-service/Dockerfile -t aiwalletplatform/identity-service:latest .
```

2. **Push to Registry**
```bash
docker push aiwalletplatform/api-gateway:latest
docker push aiwalletplatform/identity-service:latest
```

3. **Deploy to Kubernetes**
```bash
kubectl create namespace ai-wallet-platform
kubectl apply -f k8s/
```

---

## Part 10: Troubleshooting

### Common Issues

**Issue**: PostgreSQL Connection Refused
```bash
# Check if container is running
docker ps | grep postgres

# View logs
docker logs ai-wallet-postgres

# Restart
docker restart ai-wallet-postgres
```

**Issue**: Kafka Topic Not Found
```bash
# Ensure Kafka is running
docker ps | grep kafka

# Recreate topics
kafka-topics --create --topic transactions.created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1 --if-not-exists
```

**Issue**: Spring AI API Key Invalid
```bash
# Verify key format
echo $OPENAI_API_KEY | head -c 10

# Test with curl
curl https://api.openai.com/v1/models \
  -H "Authorization: Bearer $OPENAI_API_KEY"
```

**Issue**: Port Already in Use
```bash
# Find process on port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

---

## Part 11: Production Considerations

### 11.1 Security Hardening

```yaml
# Kubernetes Network Policy
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: api-gateway-netpol
spec:
  podSelector:
    matchLabels:
      app: api-gateway
  policyTypes:
  - Ingress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: client
    ports:
    - protocol: TCP
      port: 8080
```

### 11.2 Resource Limits

```yaml
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

### 11.3 Health Checks

```yaml
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 10
  
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 10
  periodSeconds: 5
```

---

## Part 12: Performance Tuning

### 12.1 JVM Tuning

```bash
# For API Gateway
java -Xms2G -Xmx4G \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Djava.util.concurrent.ForkJoinPool.common.parallelism=16 \
  -jar api-gateway.jar
```

### 12.2 Database Optimization

```sql
-- Create indexes for frequent queries
CREATE INDEX idx_transaction_user_date 
ON transactions(wallet_id, created_at DESC);

-- Analyze query plans
EXPLAIN ANALYZE SELECT * FROM transactions 
WHERE wallet_id = 'uuid' AND created_at > now() - interval '30 days';
```

### 12.3 Cache Strategy

```bash
# Redis memory policy
maxmemory-policy allkeys-lru
```

---

## Conclusion

You now have a complete, production-ready AI-Native Digital Wallet Platform showcasing:
- ✅ Enterprise Architecture
- ✅ Spring AI Integration
- ✅ Microservices Pattern
- ✅ Cloud-Native Design
- ✅ Modern Java 21 Features
- ✅ BFSI-Grade Security
- ✅ Full Observability

For questions or issues, refer to:
- [Architecture Documentation](./ARCHITECTURE.md)
- [README](./README.md)
- Service-specific documentation in each service folder
