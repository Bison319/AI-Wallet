# Technology Stack & Justification

## AI-Native Digital Wallet Platform - Complete Technology Reference

---

## 1. Core Platform

### Java 21 - Language Foundation

**Why Java 21?**
- Latest LTS release (March 2023, support until March 2031)
- Virtual Threads for high-concurrency I/O-bound services
- Records for immutable data classes (replacing verbose POJOs)
- Sealed classes for type-safe inheritance hierarchies
- Pattern matching for readable conditional logic
- Text blocks for multi-line strings (SQL, JSON templates)
- Structural concurrency improvements
- Performance improvements (G1GC, parallel GC)

**Key Features Used**:
```java
// Records - Immutable DTOs and Events
record UserDTO(UUID id, String email, String firstName) {}

// Sealed Classes - Event hierarchy type safety
sealed interface DomainEvent permits WalletEvent, TransactionEvent {...}

// Pattern Matching - Type-safe event dispatch
switch (event) {
    case WalletCreatedEvent e -> handleWallet(e);
    case TransactionEvent e -> handleTransaction(e);
    // Compiler ensures exhaustiveness!
}

// Virtual Threads - Efficient concurrency
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
executor.submit(() -> handleRequest());
```

**Justification**: 
- Future-proof (8+ years LTS support)
- Performance gains reduce infrastructure costs
- Type safety prevents entire classes of bugs
- Modern syntax matches enterprise standards

---

## 2. Application Framework

### Spring Boot 3.3.0 - Application Framework

**Version Strategy**:
- 3.3.0 (Latest stable, March 2024)
- LTS support until December 2024
- Java 21 native support
- Performance improvements over 3.2.x
- Spring AI 0.8.1 compatibility

**Core Modules**:

| Module | Version | Purpose |
|--------|---------|---------|
| spring-boot | 3.3.0 | Application bootstrap |
| spring-boot-starter-web | 3.3.0 | REST controllers |
| spring-boot-starter-data-jpa | 3.3.0 | ORM/Database |
| spring-boot-starter-security | 3.3.0 | Authentication/Authorization |
| spring-cloud-starter-gateway | 2023.0.0 | API Gateway |
| spring-cloud-starter-config | 2023.0.0 | Configuration management |
| spring-cloud-starter-netflix-eureka-client | 2023.0.0 | Service discovery |

**Why This Stack?**
- Industry standard for enterprise Java
- Mature ecosystem and community
- Excellent tooling support (Spring Tool Suite, IntelliJ)
- Extensive documentation and training
- Production-proven at scale

---

## 3. AI Integration

### Spring AI 0.8.1 - LLM Integration Framework

**Core Components**:

```
Spring AI 0.8.1
├── ChatClient         # LLM interaction interface
├── PromptTemplate     # Templated prompts
├── Embeddings        # Vector embeddings
├── VectorStore       # RAG repository
├── Advisors          # RAG + function calling
├── Structured Output # Typed responses
└── Memory            # Conversation history
```

**LLM Model Support**:

| Provider | Model | Cost | Use Case |
|----------|-------|------|----------|
| OpenAI | gpt-4 | $0.03/1K input tokens | Primary - Financial analysis |
| OpenAI | gpt-3.5-turbo | $0.0005/1K tokens | Fallback - Cost optimization |
| Anthropic | claude-3-opus | $0.015/1K tokens | Alternative - Quality |
| Azure OpenAI | gpt-4 | ~$0.03/1K tokens | Enterprise - Private VNet |
| Open Source | Mistral via Ollama | Free | On-premise - Privacy |
| Open Source | LLaMA 2 via Ollama | Free | On-premise - Cost |

**Key Features Used**:

1. **ChatClient** - Core LLM interaction
```java
// Direct chat interaction
String response = chatClient.prompt()
    .system("You are a financial advisor")
    .user("Analyze my spending")
    .call()
    .content();
```

2. **PromptTemplate** - Reusable prompts
```java
// Template with variables
String template = "Analyze spending: {amount} on {category}";
PromptTemplate pt = new PromptTemplate(template, variables);
String response = chatClient.prompt(pt).call().content();
```

3. **Conversation Memory** - Context tracking
```java
// Store/retrieve conversation history
conversationMemoryService.storeMessage(conversationId, userMsg, aiResponse);
List<Message> context = conversationMemoryService.getContextWindow(conversationId);
```

4. **Embeddings** - Semantic similarity
```java
// Future: RAG implementation
EmbeddingResponse embedding = embeddingModel.embedForIndex(text);
// Store in Milvus, search semantically
```

**Why Spring AI?**
- Unified abstraction over multiple LLM providers
- Production-grade error handling
- Token usage tracking built-in
- Conversation memory support
- Cost optimization features
- Open source and actively maintained

---

## 4. Data Persistence

### PostgreSQL 15 - Primary Database

**Configuration**:
```yaml
Version: PostgreSQL 15.2
Max Connections: 200
Shared Buffers: 256MB
Effective Cache Size: 1GB
Extensions:
  - uuid-ossp (UUID generation)
  - pgcrypto (Encryption)
  - pgvector (Vector embeddings for RAG)
  - amcheck (Data integrity)
```

**Database Structure**:
```
ai_wallet_identity      # User authentication
ai_wallet_user          # User profiles
ai_wallet_wallet        # Wallet management
ai_wallet_transaction   # Transaction history
ai_wallet_fraud         # Fraud detection
ai_wallet_investigation # RAG & investigations
ai_wallet_payment       # Payment settlement
ai_wallet_notification  # Event log
ai_wallet_analytics     # Aggregations
ai_wallet_audit         # Compliance log
ai_wallet_config        # Configuration
```

**Why PostgreSQL?**
- ACID compliance for financial transactions
- Advanced data types (JSON, arrays, custom types)
- Full-text search support
- UUID support (uuid-ossp extension)
- Vector support (pgvector for embeddings)
- Scalable (proven at 100TB+ scale)
- Cost-effective (open source)
- Strong SQL standard compliance

**Performance Optimization**:
- Connection pooling (HikariCP, 20 connections)
- Query result caching (Redis)
- Index strategy (indexed on: email, phone, user_id, wallet_id, status, created_at)
- Query optimization via EXPLAIN ANALYZE
- Partitioning strategy for large tables (monthly/yearly)

---

### Redis 7+ - Caching & Session Store

**Configuration**:
```yaml
Version: 7.2.0
Memory: 1-4GB (configurable)
Persistence: RDB snapshots + AOF
Eviction Policy: allkeys-lru (LRU removal)
Cluster: Cluster mode enabled (future)
```

**Key Use Cases**:

| Use Case | Key Pattern | TTL | Size |
|----------|-------------|-----|------|
| JWT Blacklist | `blacklist:{token}` | 24h | ~1KB |
| User Sessions | `session:{userId}` | 30m | ~5KB |
| Conversation Memory | `conversation:{id}:messages` | 24h | ~50KB |
| Rate Limit Buckets | `ratelimit:{userId}` | 1h | ~1KB |
| Cache Results | `cache:{key}:{hash}` | Variable | ~100KB |

**Why Redis?**
- Sub-millisecond latency
- In-memory performance (L1 cache level)
- Rich data types (List, Set, SortedSet, Hash)
- Pub/Sub for real-time updates
- Lua scripting for atomic operations
- Cluster support for horizontal scaling
- Cost-effective for moderate datasets

---

### Elasticsearch 8.10+ - Full-Text Search

**Use Cases**:
1. **Transaction Search** - Search transaction descriptions
2. **Audit Log Query** - Find actions by user/resource
3. **Investigation Context** - Full-text search prior transactions
4. **Anomaly Detection** - Pattern identification

**Configuration**:
```yaml
Version: 8.10.0
Shards: 3 (for scalability)
Replicas: 1 (for HA)
Heap: 512MB-1GB
Analysis: Standard + custom analyzers
```

**Why Elasticsearch?**
- Production-grade search engine
- Horizontal scaling via sharding
- Relevance scoring (BM25)
- Aggregations for analytics
- TLS/Authentication built-in
- Proven at petabyte scale

---

### Milvus - Vector Database for RAG

**Purpose**: Semantic search for Investigation Service

**Configuration**:
```yaml
Version: 2.3.0 (latest stable)
Vector Dimension: 1536 (OpenAI embeddings)
Index Type: HNSW (Hierarchical Navigable Small World)
Similarity Metric: L2 (Euclidean distance)
```

**Data Model**:
```
Investigation Vector DB
├── Transaction Embeddings (from transaction.description)
├── User Profile Vectors (from KYC data)
├── Fraud Pattern Vectors (from historical alerts)
└── Metadata (transaction_id, user_id, timestamp)
```

**RAG Query Flow**:
```
User Query → OpenAI Embeddings → Milvus Search → 
Top-10 Results → LLM Context → Investigation Report
```

**Why Milvus?**
- Purpose-built for vector search
- HNSW algorithm for speed and accuracy
- Open source (no vendor lock-in)
- Scales to billions of vectors
- ACID compliance for transactions
- Active community support

---

## 5. Event Streaming

### Apache Kafka 3.x - Event Bus

**Configuration**:
```yaml
Version: 3.6.0 (latest stable)
Broker Count: 3 (HA)
Replication Factor: 3 (durability)
Partitions: 3 per topic (parallelism)
Retention: 7 days
Compression: snappy
```

**Topics**:

| Topic | Partitions | Purpose | Consumers |
|-------|-----------|---------|-----------|
| `users.created` | 3 | User registration | Analytics, Notification |
| `users.kyc-completed` | 3 | KYC completion | Fraud, Analytics |
| `wallets.created` | 3 | Wallet creation | Analytics |
| `wallets.limits-updated` | 3 | Limit changes | Notification |
| `transactions.initiated` | 5 | New transaction | Fraud detection |
| `transactions.completed` | 5 | Transaction success | Analytics |
| `transactions.failed` | 3 | Transaction failure | Retry, Notification |
| `fraud.alerts-created` | 3 | Fraud alert | Investigation, Notification |
| `fraud.alerts-escalated` | 3 | Escalation | Compliance, Admin |
| `payments.initiated` | 3 | Payment start | Settlement |
| `payments.settled` | 3 | Payment success | Accounting |

**Consumer Groups**:
- `wallet-consumer-group`
- `fraud-consumer-group`
- `notification-consumer-group`
- `analytics-consumer-group`
- `audit-consumer-group`

**Why Kafka?**
- Proven at scale (Uber, Netflix, LinkedIn)
- Fault-tolerant (replication, replica election)
- Exactly-once semantics available
- Consumer group offset management
- Pub/Sub and event sourcing capable
- Log compaction for state stores
- Schema registry ready

---

## 6. Infrastructure

### Docker & Docker Compose - Local Development

**Services**:
```
PostgreSQL:9.3 → Port 5432
Redis:7        → Port 6379
Kafka:3        → Port 9092
Zookeeper:3    → Port 2181
Milvus:2.3     → Ports 19530, 9091
Elasticsearch:8 → Port 9200
Prometheus:2   → Port 9090
Grafana:10     → Port 3000
Jaeger:1       → Port 16686
PgAdmin:7      → Port 5050
Mailhog        → Ports 1025 (SMTP), 8025 (Web)
```

**Why Docker Compose?**
- One-command development environment
- No manual service installation
- Reproducible across developers
- Easy cleanup (docker-compose down)
- Volume management for persistence
- Network isolation between services
- Health checks for readiness

---

### Kubernetes - Production Deployment

**Components**:

| Component | Purpose | Implementation |
|-----------|---------|-----------------|
| Deployments | Service replicas | 3 initial, HPA 3-10 |
| Services | Load balancing | ClusterIP (internal), LoadBalancer (gateway) |
| ConfigMaps | Configuration | Environment-specific settings |
| Secrets | Credentials | API keys, certificates |
| PersistentVolumes | Data storage | PostgreSQL, Elasticsearch |
| NetworkPolicies | Security | Pod-to-pod communication rules |
| RBAC | Access control | Least privilege per service |
| Ingress | External access | API gateway entry point |

**Why Kubernetes?**
- Industry standard for container orchestration
- Auto-scaling and self-healing
- Rolling updates with zero downtime
- Resource management and limits
- Service discovery built-in
- Observability integrations
- Multi-region deployment ready

---

## 7. Monitoring & Observability

### Prometheus - Metrics Collection

**Configuration**:
```yaml
Scrape Interval: 15s
Evaluation Interval: 15s
Retention: 15 days
```

**Key Metrics**:
```
# HTTP Metrics
http_requests_total{method="POST", endpoint="/api/v1/auth/login"}
http_request_duration_seconds{quantile="0.95", endpoint="/api/v1/assistant/chat"}

# Database Metrics
db_query_duration_seconds{query_type="SELECT"}
db_connections_active{pool="main"}

# AI Metrics
ai_chat_latency_seconds{model="gpt-4"}
ai_token_usage_total{model="gpt-4", type="input"}

# JVM Metrics
jvm_memory_used_bytes{area="heap"}
jvm_threads_live
gc_duration_seconds{action="end", cause="G1 Evacuation Pause"}

# Kafka Metrics
kafka_consumer_lag_sum{topic="transactions.initiated"}
```

**Why Prometheus?**
- Time-series database optimized for metrics
- Efficient storage and querying
- Pull-based model (simpler than push)
- Label-based dimension support
- PromQL query language
- Active alerting system
- Wide integration support

---

### Grafana - Visualization

**Default Dashboards**:
1. **Application Health** - JVM, memory, threads
2. **API Performance** - Latency, throughput, errors
3. **Database Health** - Connections, query times
4. **Kafka Metrics** - Consumer lag, throughput
5. **AI Performance** - Model latency, token usage
6. **Business Metrics** - Transactions, users, fraud

**Alerting Rules**:
```promql
# Example alerts
- API latency p99 > 1s
- Error rate > 5%
- DB query > 500ms
- Memory usage > 80%
- Consumer lag > 10000
```

**Why Grafana?**
- Rich visualization options
- Interactive dashboards
- Alert management
- Multi-datasource support
- Permission and access control
- Mobile app available
- Active community

---

### Jaeger - Distributed Tracing

**Configuration**:
```yaml
Sampling: 10% (adaptive sampling)
Backend: Elasticsearch (storage)
UI: Port 16686
```

**Trace Example**:
```
API Gateway Request
  ├── JWT Validation (2ms)
  ├── Rate Limit Check (1ms)
  └── Forward to Service
      ├── Spring AI ChatClient (1500ms)
      │   ├── LLM Request (1400ms)
      │   ├── Response Parsing (50ms)
      │   └── Token Counting (50ms)
      ├── Memory Store Update (20ms)
      └── Response (5ms)
Total Latency: 1528ms
```

**Why Jaeger?**
- Distributed tracing standard (OpenTelemetry)
- Service dependency mapping
- Bottleneck identification
- Latency optimization
- Error root cause analysis
- Elasticsearch integration (scalable)
- UI for trace visualization

---

## 8. Security & Compliance

### Spring Security - Authentication & Authorization

**Implementation**:
```
User Login
    ↓
Spring Security AuthenticationManager
    ↓
UserDetailsService (Database lookup)
    ↓
BCryptPasswordEncoder (Verify password)
    ↓
JwtTokenProvider (Generate token)
    ↓
Redis BlackList (Token revocation)
```

**Security Features**:
- ✅ OAuth2 resource server configuration
- ✅ JWT bearer token validation
- ✅ RBAC (Role-Based Access Control)
- ✅ CORS configuration
- ✅ CSRF protection (when applicable)
- ✅ XSS protection
- ✅ Security headers

---

### OpenTelemetry - Standards-Based Observability

**Components**:
```
Instrumentation
├── Spring Boot auto-instrumentation
├── HTTP client/server
├── Database drivers
├── Message queue
└── Cache layers
    ↓
Traces + Metrics + Logs
    ↓
Exporters
├── Jaeger (traces)
├── Prometheus (metrics)
└── ELK Stack (logs)
```

**Why OpenTelemetry?**
- Vendor-neutral standard
- No lock-in to specific tools
- Comprehensive instrumentation
- Community-driven development
- Industry adoption (Kubernetes, Google Cloud, AWS)

---

## 9. Testing & Quality

### Testing Stack

| Framework | Purpose | Use Case |
|-----------|---------|----------|
| JUnit 5 | Unit testing | Service/util methods |
| Mockito | Mocking | External dependencies |
| TestContainers | Integration | Real database tests |
| MockMvc | Controller testing | HTTP endpoint tests |
| RestAssured | API testing | Integration tests |

**Example Test**:
```java
@SpringBootTest
@TestcontainersTest
class UserAuthServiceTest {
    
    @Container
    static PostgreSQLContainer db = new PostgreSQLContainer<>();
    
    @Test
    void testUserRegistration() {
        // Arrange
        RegisterRequest req = new RegisterRequest("user@example.com", "password", ...);
        
        // Act
        UserResponse response = authService.register(req);
        
        // Assert
        assertNotNull(response.id());
        assertEquals("user@example.com", response.email());
    }
}
```

---

### Code Quality

| Tool | Purpose | Metric |
|------|---------|--------|
| JaCoCo | Code coverage | Target: >80% |
| SonarQube | Code quality | A rating |
| SpotBugs | Bug detection | 0 bugs in prod |
| Checkstyle | Code style | Consistent formatting |
| ArchUnit | Architecture | Layer enforcement |

---

## 10. Development Tools

### Maven - Build Management

**Key Plugins**:
```xml
<!-- Compiler for Java 21 -->
<maven-compiler-plugin>
  <source>21</source>
  <target>21</target>
</maven-compiler-plugin>

<!-- Spring Boot packaging -->
<spring-boot-maven-plugin>
  <mainClass>com.aiwalletplatform.Application</mainClass>
</spring-boot-maven-plugin>

<!-- Docker image building -->
<jib-maven-plugin>
  <image>aiwalletplatform/${project.artifactId}:${project.version}</image>
</jib-maven-plugin>

<!-- Code coverage -->
<jacoco-maven-plugin>
  <excludes>**/config/**,**/entity/**</excludes>
</jacoco-maven-plugin>
```

---

## 11. Dependency Versions Summary

```xml
<project>
  <properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    
    <spring-boot.version>3.3.0</spring-boot.version>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <spring-ai.version>0.8.1</spring-ai.version>
    
    <postgresql.version>42.7.0</postgresql.version>
    <redis.version>1.3.0</redis.version>
    <elasticsearch.version>8.10.0</elasticsearch.version>
    
    <kafka.version>3.6.0</kafka.version>
    <jjwt.version>0.12.3</jjwt.version>
    
    <junit.version>5.10.0</junit.version>
    <mockito.version>5.5.0</mockito.version>
    <testcontainers.version>1.19.0</testcontainers.version>
  </properties>
</project>
```

---

## 12. Cost Analysis

**Infrastructure Costs (Monthly Estimate)**:

| Component | Usage | Cost | Notes |
|-----------|-------|------|-------|
| **Compute (Kubernetes)** | 9 nodes × 2vCPU | $300 | 3 replicas × 3 services |
| **Database (PostgreSQL)** | 500GB RDS | $150 | Multi-AZ for HA |
| **Cache (Redis)** | 10GB | $50 | Managed Redis |
| **Search (Elasticsearch)** | 100GB | $80 | Managed service |
| **Kafka** | 3 brokers | $100 | Managed service |
| **AI (OpenAI)** | ~100M tokens/month | $500-2000 | Depends on model and usage |
| **Monitoring** | Prometheus/Grafana | $50 | Self-hosted |
| **Observability** | Jaeger | $50 | Self-hosted |
| **Load Balancer** | ALB/NLB | $30 | AWS |
| **DNS + CDN** | Route53 + CloudFront | $20 | AWS |
| **Total** | | **$1,330-2,830** | Including AI costs |

**Cost Optimization**:
- Use Reserved Instances (30-40% discount)
- Spot instances for non-critical workloads
- Cache aggressively (Redis)
- Compress data (Kafka compression)
- Use open-source alternatives (Milvus vs Pinecone)
- Auto-scaling to match demand

---

## 13. Performance Targets

**Latency Targets**:

| Operation | P50 | P95 | P99 | Notes |
|-----------|-----|-----|-----|-------|
| User Login | 50ms | 100ms | 200ms | JWT generation, DB lookup |
| AI Chat | 1s | 2s | 3s | LLM latency dominant |
| Transaction Query | 10ms | 50ms | 100ms | Cached results |
| Fraud Detection | 100ms | 500ms | 1s | ML model inference |
| RAG Query | 500ms | 2s | 5s | Vector search + LLM |

**Throughput Targets**:

| Operation | Target | Implementation |
|-----------|--------|-----------------|
| API Gateway | 10K req/sec | Virtual Threads, load balancing |
| Database | 100 req/sec/instance | Connection pooling, caching |
| Kafka | 1M msg/sec | Partitioning, compression |
| Search | 1K queries/sec | Sharding, caching |
| AI | 10 concurrent chats | Rate limiting, queuing |

---

## Conclusion

This technology stack represents:
- **Production-Proven**: Each component is production-grade
- **Enterprise-Ready**: Standards and patterns from FAANG companies
- **Cost-Optimized**: Balance between performance and cost
- **Scalable**: Horizontal scaling across all tiers
- **Secure**: Security at every layer
- **Observable**: Comprehensive monitoring and tracing

**Next Steps**:
1. Implement remaining services using this pattern
2. Load test with expected production traffic
3. Security audit by external firm
4. Cost optimization with actual usage metrics
5. CI/CD pipeline for automated deployment
