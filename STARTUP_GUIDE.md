# Complete Service Startup Guide

This guide provides detailed instructions for starting all services in the AI Wallet Platform.

## Prerequisites

- Java 21 JDK
- Maven 3.9+
- Docker & Docker Compose
- Node.js 18+
- Git

## Infrastructure Setup

### 1. Start Docker Compose Services

```bash
docker-compose up -d
```

This starts:
- PostgreSQL (5432)
- Redis (6379)
- Kafka + Zookeeper (9092)
- Milvus Vector DB (19530)
- Elasticsearch (9200)
- Prometheus (9090)
- Grafana (3000) - admin/admin
- Jaeger UI (16686)
- PgAdmin (5050) - postgres/postgres

### 2. Verify Services

```bash
docker-compose ps
docker-compose logs -f
```

## Building the Project

### Build All Services

```bash
mvn clean install -DskipTests
```

### Build Specific Service

```bash
cd services/wallet-service
mvn clean package
```

## Running Services Locally

### Startup Sequence

Services must be started in this order due to dependencies:

#### 1. Discovery Server (Port 8761)

```bash
cd services/discovery-server
mvn spring-boot:run
```

**Access:** http://localhost:8761

#### 2. Config Server (Port 8888)

```bash
cd services/config-server
mvn spring-boot:run
```

**Access:** http://localhost:8888

#### 3. API Gateway (Port 8080)

```bash
cd services/api-gateway
mvn spring-boot:run
```

**Access:** http://localhost:8080

#### 4. Identity Service (Port 8081)

```bash
cd services/identity-service
mvn spring-boot:run
```

#### 5. User Service (Port 8082)

```bash
cd services/user-service
mvn spring-boot:run
```

#### 6. Wallet Service (Port 8083)

```bash
cd services/wallet-service
mvn spring-boot:run
```

#### 7. Transaction Service (Port 8084)

```bash
cd services/transaction-service
mvn spring-boot:run
```

#### 8. Payment Service (Port 8085)

```bash
cd services/payment-service
mvn spring-boot:run
```

#### 9. Fraud Detection Service (Port 8086)

```bash
cd services/fraud-detection-service
mvn spring-boot:run
```

**Note:** Requires OPENAI_API_KEY environment variable

#### 10. Investigation Service (Port 8087)

```bash
cd services/investigation-service
mvn spring-boot:run
```

**Note:** Requires OPENAI_API_KEY environment variable

#### 11. Notification Service (Port 8088)

```bash
cd services/notification-service
mvn spring-boot:run
```

#### 12. Analytics Service (Port 8090)

```bash
cd services/analytics-service
mvn spring-boot:run
```

#### 13. Audit Service (Port 8091)

```bash
cd services/audit-service
mvn spring-boot:run
```

### Run All Services with Maven

Alternative: Run all services in parallel using Maven:

```bash
mvn -pl services/discovery-server,services/config-server spring-boot:run &
sleep 5
mvn -pl services/api-gateway,services/identity-service,services/user-service spring-boot:run &
sleep 5
mvn -pl services/wallet-service,services/transaction-service spring-boot:run &
# ... continue with others
```

## Frontend Setup

### Install Dependencies

```bash
cd frontend
npm install
```

### Development Server

```bash
npm start
```

**Access:** http://localhost:3000

### Production Build

```bash
npm run build
serve -s build
```

## Testing the API

### 1. Register User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+919876543210"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

**Response contains:** `accessToken`, `refreshToken`, `userId`

### 3. Create Wallet

```bash
curl -X POST "http://localhost:8080/api/v1/wallets?userId=<userId>&walletName=My%20Wallet" \
  -H "Authorization: Bearer <accessToken>"
```

### 4. Get Wallet

```bash
curl -X GET "http://localhost:8080/api/v1/wallets/user/<userId>" \
  -H "Authorization: Bearer <accessToken>"
```

### 5. Record Transaction

```bash
curl -X POST "http://localhost:8080/api/v1/transactions?walletId=<walletId>&userId=<userId>&description=Test&amount=100&type=DEBIT" \
  -H "Authorization: Bearer <accessToken>"
```

## Docker Deployment

### Build Docker Images

```bash
# Build specific service
docker build -t ai-wallet/wallet-service:1.0.0 services/wallet-service/

# Build all services
./scripts/build-docker-images.sh
```

### Run with Docker Compose

```bash
docker-compose -f docker-compose.prod.yml up -d
```

## Kubernetes Deployment

### Prerequisites

- kubectl configured
- Kubernetes cluster (kind, minikube, or cloud)

### Deploy to Kubernetes

```bash
# Apply deployments
kubectl apply -f k8s/

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/api-gateway
```

## Monitoring & Observability

### Prometheus

**URL:** http://localhost:9090

**Metrics:** http://localhost:8080/actuator/metrics

### Grafana

**URL:** http://localhost:3000  
**Username:** admin  
**Password:** admin

### Jaeger Tracing

**URL:** http://localhost:16686

### ELK Stack (Elasticsearch)

**URL:** http://localhost:9200

## Troubleshooting

### Port Already in Use

```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Issues

```bash
# Check PostgreSQL
psql -h localhost -U postgres -d ai_wallet_identity

# Create databases manually
psql -h localhost -U postgres -c "CREATE DATABASE ai_wallet_identity;"
psql -h localhost -U postgres -c "CREATE DATABASE ai_wallet_user;"
psql -h localhost -U postgres -c "CREATE DATABASE ai_wallet_wallet;"
```

### Kafka Issues

```bash
# Check Kafka status
docker logs ai-wallet-platform-kafka-1

# Reset topics
docker exec -it kafka kafka-topics --delete --bootstrap-server localhost:9092 --topic wallet-events
docker exec -it kafka kafka-topics --create --bootstrap-server localhost:9092 --topic wallet-events --partitions 3
```

### Clear Everything and Start Fresh

```bash
docker-compose down -v
mvn clean
docker-compose up -d
# Wait a moment for services to be ready
mvn clean install -DskipTests
# Start services
```

## Performance Tuning

### JVM Options

```bash
export JAVA_OPTS="-Xmx2G -Xms1G -XX:+UseG1GC"
mvn spring-boot:run
```

### Database Optimization

```sql
CREATE INDEX idx_wallet_user_id ON wallets(user_id);
CREATE INDEX idx_transaction_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transaction_created_at ON transactions(created_at);
```

## Next Steps

1. Configure Spring AI with your OpenAI API key
2. Set up proper environment variables in `.env`
3. Configure email/SMS providers for notifications
4. Set up monitoring dashboards in Grafana
5. Configure log aggregation (ELK Stack)
6. Set up CI/CD pipeline (GitHub Actions, GitLab CI)
7. Deploy to Kubernetes
