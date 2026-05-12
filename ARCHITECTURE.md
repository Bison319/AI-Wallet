# AI-Native Digital Wallet Platform - Enterprise Architecture

## Executive Summary

This is a production-grade, AI-first fintech platform demonstrating enterprise-scale architecture for a BFSI (Banking, Financial Services, and Insurance) digital wallet ecosystem. The platform integrates Spring AI capabilities for intelligent transaction analysis, fraud detection, and conversational financial assistance.

---

## 1. Strategic Architecture Overview

### 1.1 Architectural Paradigms

```
┌─────────────────────────────────────────────────────────┐
│         CLIENT LAYER (React + TypeScript)               │
├─────────────────────────────────────────────────────────┤
│           API GATEWAY (Spring Cloud Gateway)            │
├─────────────────────────────────────────────────────────┤
│  SERVICE MESH (Istio) - Service-to-Service Communication │
├─────────────────────────────────────────────────────────┤
│  MICROSERVICES LAYER (13 Independent Services)          │
│  ├─ Identity Service (OAuth2/JWT)                       │
│  ├─ User Service (DDD)                                  │
│  ├─ Wallet Service (CQRS)                               │
│  ├─ Transaction Service (Event-Sourced)                 │
│  ├─ Payment Service (Saga Orchestration)                │
│  ├─ Fraud Detection (AI-Powered)                        │
│  ├─ AI Assistant (Spring AI ChatClient)                 │
│  ├─ Investigation Service (RAG + Semantic Search)       │
│  ├─ Notification Service (Event-Driven)                 │
│  ├─ Analytics Service (OLAP)                            │
│  ├─ Audit Service (Immutable Ledger)                    │
│  ├─ Config Server (Centralized Configuration)           │
│  └─ Discovery Server (Service Registry)                 │
├─────────────────────────────────────────────────────────┤
│        DATA LAYER & PERSISTENCE                         │
│  ├─ PostgreSQL (Transactional Data)                     │
│  ├─ Redis (Caching & Sessions)                          │
│  ├─ Elasticsearch (Full-text & Analytics)               │
│  ├─ Vector DB (Pinecone/Milvus - Embeddings)            │
│  └─ pgvector (PostgreSQL Vector Extension)              │
├─────────────────────────────────────────────────────────┤
│     EVENT STREAMING & ASYNC COMMUNICATION               │
│  └─ Apache Kafka (Event Bus, Event Sourcing)            │
├─────────────────────────────────────────────────────────┤
│    INFRASTRUCTURE (Kubernetes, Helm, Docker)            │
├─────────────────────────────────────────────────────────┤
│  OBSERVABILITY (OpenTelemetry, Grafana, Prometheus)     │
└─────────────────────────────────────────────────────────┘
```

### 1.2 Architectural Principles

1. **Clean Architecture** - Separation of concerns across layers
2. **Hexagonal (Ports & Adapters)** - External dependencies as pluggable adapters
3. **Domain-Driven Design (DDD)** - Business logic at the core
4. **Event-Driven Architecture** - Asynchronous, loosely-coupled services
5. **CQRS Pattern** - Separate read and write models for scalability
6. **Saga Pattern** - Distributed transaction management
7. **API Gateway** - Single entry point, request routing, security
8. **Service Mesh** - Inter-service communication, resilience
9. **Cloud-Native** - Designed for Kubernetes, 12-factor principles

---

## 2. Domain-Driven Design (DDD) Bounded Contexts

### 2.1 Bounded Context Map

```
┌──────────────────────────────────┐
│   IDENTITY & SECURITY CONTEXT    │  → Manages authentication, authorization
│   ├─ OAuth2 Provider              │
│   ├─ JWT Token Management        │
│   └─ RBAC Enforcement            │
└──────────────┬───────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
┌───▼──────────────────┐  ┌──────────────────────────┐
│  USER CONTEXT       │  │  WALLET CONTEXT          │
│  ├─ User Profile    │  │  ├─ Wallet Aggregates   │
│  ├─ KYC/AML         │  │  ├─ Balance Management  │
│  └─ Preferences     │  │  └─ Wallet Operations   │
└───┬──────────────────┘  └─────┬──────────────────┘
    │                           │
    │                    ┌──────┴──────┐
    │                    │             │
    │            ┌───────▼──────┐  ┌──▼────────────────┐
    │            │ TRANSACTION  │  │ PAYMENT CONTEXT   │
    │            │ CONTEXT      │  │ ├─ Payment Orders │
    │            │ ├─ Ledger    │  │ ├─ Settlement     │
    │            │ ├─ History   │  │ └─ Reconciliation │
    │            │ └─ Events    │  └───────────────────┘
    │            └───────┬──────┘
    │                    │
    │    ┌───────────────┴──────────────┐
    │    │                              │
    ├────▼──────────────┐  ┌──────────▼────────────┐
    │ FRAUD DETECTION   │  │ INVESTIGATION CONTEXT │
    │ CONTEXT           │  │ ├─ Entity Resolution │
    │ ├─ Risk Scoring   │  │ ├─ RAG Query Engine  │
    │ ├─ ML Models      │  │ └─ Compliance Audit  │
    │ └─ Alerts         │  └──────────────────────┘
    └─────────┬─────────┘
              │
    ┌─────────▼──────────┐
    │ AI ASSISTANT       │
    │ CONTEXT            │
    │ ├─ Conversations   │
    │ ├─ Prompts         │
    │ └─ Memory          │
    └────────────────────┘
```

### 2.2 Bounded Context Details

| Context | Responsibility | Data Store | Key Entities |
|---------|---|---|---|
| **Identity** | Authentication, Authorization | PostgreSQL | User, Role, Permission |
| **User** | Profile, KYC, Preferences | PostgreSQL | UserProfile, KYCData, Preferences |
| **Wallet** | Wallet lifecycle, balance | PostgreSQL + Redis | Wallet, WalletAccount, Balance |
| **Transaction** | Immutable transaction record | PostgreSQL + Kafka | Transaction, TransactionEvent |
| **Payment** | Payment orchestration | PostgreSQL | PaymentOrder, Settlement |
| **Fraud Detection** | Risk assessment, anomaly | PostgreSQL + ML | FraudAlert, RiskScore, Behavior |
| **Investigation** | Compliance, forensics | Elasticsearch + Vector DB | InvestigationCase, Evidence |
| **AI Assistant** | Conversational interface | Redis + Vector DB | Conversation, Context |
| **Notification** | Event-based notifications | PostgreSQL | NotificationEvent, Template |
| **Analytics** | Business intelligence | Elasticsearch | MetricEvent, AggregatedData |
| **Audit** | Immutable audit trail | PostgreSQL | AuditLog, ComplianceRecord |

---

## 3. Event Storming Output

### 3.1 Core Event Flow

```
USER ACTION
    │
    ├─→ CREATE_WALLET_REQUESTED
    │   └─→ WalletService: validateUser()
    │       └─→ Wallet: createWallet()
    │           └─→ [EVENT] WalletCreatedEvent
    │               ├─→ UserService: updateUserWalletReference()
    │               ├─→ NotificationService: sendWalletCreationNotification()
    │               └─→ AuditService: logWalletCreation()
    │
    ├─→ TRANSACTION_INITIATED
    │   └─→ TransactionService: createTransaction()
    │       ├─→ WalletService: deductBalance()
    │       ├─→ FraudDetectionService: analyzeTransaction()
    │       │   └─→ AI: evaluateAnomalies()
    │       └─→ [EVENT] TransactionProcessedEvent
    │           ├─→ PaymentService: initiateClearingSettlement()
    │           ├─→ NotificationService: sendTransactionAlert()
    │           ├─→ AnalyticsService: recordMetric()
    │           ├─→ AuditService: logTransaction()
    │           └─→ VectorDB: embedTransaction() [for RAG]
    │
    └─→ INVESTIGATION_INITIATED
        └─→ InvestigationService: queryRAG()
            ├─→ SemanticSearch: findRelatedTransactions()
            ├─→ AI Assistant: generateInvestigationReport()
            └─→ [EVENT] InvestigationCompletedEvent
                └─→ NotificationService: alertCompliance()
```

### 3.2 Key Event Types

```
WALLET EVENTS:
- WalletCreatedEvent
- WalletActivatedEvent
- WalletDeactivatedEvent
- WalletLimitUpdatedEvent

TRANSACTION EVENTS:
- TransactionInitiatedEvent
- TransactionProcessingEvent
- TransactionCompletedEvent
- TransactionFailedEvent
- TransactionReversedEvent

FRAUD EVENTS:
- SuspiciousActivityDetectedEvent
- FraudAlertEscalatedEvent
- AnomalyConfirmedEvent

PAYMENT EVENTS:
- PaymentOrderCreatedEvent
- PaymentSettledEvent
- PaymentFailedEvent

INVESTIGATION EVENTS:
- InvestigationInitiatedEvent
- EvidenceCollectedEvent
- InvestigationCompletedEvent

AI EVENTS:
- ConversationStartedEvent
- QueryProcessedEvent
- ResponseGeneratedEvent
```

---

## 4. Microservices Decomposition

### 4.1 Service-Responsibility Matrix

```
SERVICE                  │ PRIMARY RESPONSIBILITY           │ DOMAIN
────────────────────────┼──────────────────────────────────┼──────────────────
API Gateway             │ Routing, Authentication, Rate    │ Infrastructure
                        │ Limiting                          │
────────────────────────┼──────────────────────────────────┼──────────────────
Identity Service        │ OAuth2, JWT, RBAC               │ Security
────────────────────────┼──────────────────────────────────┼──────────────────
User Service            │ User Profiles, KYC, Preferences  │ User Management
────────────────────────┼──────────────────────────────────┼──────────────────
Wallet Service          │ Wallet CRUD, Balance Mgmt        │ Core Wallet
────────────────────────┼──────────────────────────────────┼──────────────────
Transaction Service     │ Transaction Recording, History   │ Transactions
────────────────────────┼──────────────────────────────────┼──────────────────
Payment Service         │ Payment Orchestration, Settlement│ Payments
────────────────────────┼──────────────────────────────────┼──────────────────
Fraud Detection         │ Risk Analysis, Anomaly Detection │ AI/Security
────────────────────────┼──────────────────────────────────┼──────────────────
AI Assistant            │ Conversational Finance Advice    │ AI
────────────────────────┼──────────────────────────────────┼──────────────────
Investigation           │ Forensic Analysis, Compliance    │ AI/Compliance
────────────────────────┼──────────────────────────────────┼──────────────────
Notification            │ Event-based Messaging            │ Communication
────────────────────────┼──────────────────────────────────┼──────────────────
Analytics               │ Metrics, Business Intelligence   │ Analytics
────────────────────────┼──────────────────────────────────┼──────────────────
Audit                   │ Immutable Audit Trail            │ Compliance
────────────────────────┼──────────────────────────────────┼──────────────────
Config Server           │ Centralized Configuration        │ Infrastructure
────────────────────────┼──────────────────────────────────┼──────────────────
Discovery Server        │ Service Registry, Health Checks  │ Infrastructure
```

---

## 5. Spring AI Integration Architecture

### 5.1 AI System Design

```
┌─────────────────────────────────────────────────────────┐
│              SPRING AI LAYER                            │
├─────────────────────────────────────────────────────────┤
│  ┌────────────────┐  ┌────────────────┐  ┌──────────┐ │
│  │ ChatClient     │  │ Advisors       │  │ RAG      │ │
│  │ (Assistant)    │  │ (Retrieval)    │  │ Pipeline │ │
│  └────────────────┘  └────────────────┘  └──────────┘ │
├─────────────────────────────────────────────────────────┤
│  ┌────────────────┐  ┌────────────────┐  ┌──────────┐ │
│  │ Prompt         │  │ Memory         │  │ Tool     │ │
│  │ Management     │  │ Management     │  │ Calling  │ │
│  └────────────────┘  └────────────────┘  └──────────┘ │
├─────────────────────────────────────────────────────────┤
│              LLM PROVIDERS (OpenAI/Anthropic)           │
├─────────────────────────────────────────────────────────┤
│   ┌─────────────┐  ┌──────────────┐  ┌──────────────┐ │
│   │ Embeddings  │  │ Vector Store │  │ Retrieval    │ │
│   │ Models      │  │ (Milvus)     │  │ Strategy     │ │
│   └─────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

### 5.2 Three Pillar AI Features

#### **Pillar 1: AI Financial Assistant**
- **Purpose**: Conversational financial guidance
- **Technology**: Spring AI ChatClient + Memory
- **Capabilities**:
  - Transaction explanation
  - Spending insights
  - Budget recommendations
  - Financial health analysis
- **Data**: Current wallet state, recent transactions
- **Model**: OpenAI GPT-4 / Anthropic Claude

#### **Pillar 2: Fraud Detection Engine**
- **Purpose**: Real-time anomaly detection
- **Technology**: Spring AI Structured Outputs + Custom ML
- **Capabilities**:
  - Velocity checks
  - Geo anomaly detection
  - Behavioral pattern analysis
  - Risk scoring with explanation
- **Data**: Historical transactions, user behavior profile
- **Model**: Custom ML + LLM reasoning

#### **Pillar 3: Investigation Copilot (RAG)**
- **Purpose**: Enterprise forensic analysis
- **Technology**: Spring AI RAG + VectorStore
- **Capabilities**:
  - Semantic transaction search
  - Relationship analysis
  - Compliance investigation
  - Evidence correlation
- **Data**: All transactions (embedded), user profiles, patterns
- **Model**: RAG pipeline with semantic retrieval

---

## 6. RAG Architecture Deep Dive

### 6.1 RAG Pipeline

```
INVESTIGATION QUERY
    │
    ├─→ Preprocessing
    │   └─→ Normalize & tokenize query
    │
    ├─→ Embedding Generation
    │   └─→ Spring AI Embedding Model
    │       └─→ OpenAI Embeddings API
    │
    ├─→ Vector Search
    │   ├─→ Milvus Vector Database
    │   └─→ Top-K nearest neighbors
    │
    ├─→ Context Retrieval
    │   ├─→ Transaction documents
    │   ├─→ User profiles
    │   └─→ Pattern analysis
    │
    ├─→ Prompt Augmentation
    │   └─→ Inject context into system prompt
    │
    ├─→ LLM Inference
    │   └─→ Generate investigation report
    │
    └─→ Response Post-Processing
        ├─→ Format output
        ├─→ Add citations
        └─→ Generate visualizations
```

### 6.2 Vector Store Strategy

**Milvus Deployment**:
- Stores transaction embeddings (768-dim)
- Stores user profile embeddings
- Stores behavioral pattern vectors
- Supports hybrid search (vector + scalar)
- Horizontal scalability

**Query Strategy**:
1. Dense vector search (semantic similarity)
2. Scalar filtering (amount range, date range)
3. Re-ranking with cross-encoders
4. Context aggregation

---

## 7. Data Flow Architecture

### 7.1 Write Path (Transactions)

```
CLIENT
    │
    ├─→ API Gateway
    │
    ├─→ Transaction Service
    │   ├─→ Validate request
    │   ├─→ Create Transaction entity
    │   ├─→ Publish TransactionCreatedEvent
    │   │   ├─→ Kafka Topic: transactions.created
    │   │   └─→ PostgreSQL: transactions table
    │   │
    │   └─→ PARALLEL PROCESSING
    │       ├─→ Fraud Detection Service
    │       │   ├─→ Analyze anomalies
    │       │   └─→ Generate risk score
    │       │
    │       ├─→ Payment Service
    │       │   ├─→ Initiate clearing
    │       │   └─→ Settlement orchestration
    │       │
    │       ├─→ Notification Service
    │       │   └─→ Send alerts
    │       │
    │       ├─→ Analytics Service
    │       │   └─→ Record metrics
    │       │
    │       ├─→ Audit Service
    │       │   └─→ Create audit log
    │       │
    │       └─→ Investigation Service
    │           ├─→ Generate embeddings
    │           └─→ Store in Vector DB
    │
    └─→ Response to Client
```

### 7.2 Read Path (Investigation RAG Query)

```
CLIENT (Investigation Request)
    │
    ├─→ API Gateway
    │
    ├─→ Investigation Service
    │   ├─→ Parse query (natural language)
    │   ├─→ Generate query embedding
    │   │
    │   ├─→ Vector Search (Milvus)
    │   │   ├─→ Retrieve top-K similar transactions
    │   │   ├─→ Apply scalar filters
    │   │   └─→ Re-rank results
    │   │
    │   ├─→ Context Assembly
    │   │   ├─→ Fetch user profiles
    │   │   ├─→ Fetch related transactions
    │   │   └─→ Build relationship graph
    │   │
    │   ├─→ Spring AI RAG Pipeline
    │   │   ├─→ Prompt template: "Given the following context..."
    │   │   ├─→ Inject retrieved documents
    │   │   └─→ LLM generation: Investigation report
    │   │
    │   └─→ Response Assembly
    │       ├─→ Structured investigation report
    │       ├─→ Evidence citations
    │       ├─→ Risk assessment
    │       └─→ Recommendations
    │
    └─→ Response to Client
```

---

## 8. Spring Cloud & Kafka Integration

### 8.1 Inter-Service Communication

**Synchronous (REST)**:
- Service-to-service queries
- Low-latency requirements
- Via Service Mesh (Istio)

**Asynchronous (Kafka)**:
- Event publishing
- Cross-bounded context communication
- Event sourcing
- Audit trail

### 8.2 Kafka Topic Structure

```
transactions.created
├─→ Consumers: FraudDetection, Payment, Notification, Analytics, Audit, Investigation

wallets.updated
├─→ Consumers: User, Notification, Analytics

fraudAlerts.detected
├─→ Consumers: Notification, Investigation, Audit

payments.settled
├─→ Consumers: Transaction, Notification, Analytics, Audit

investigations.completed
├─→ Consumers: Audit, Notification
```

---

## 9. Security Architecture

### 9.1 Security Layers

```
PERIMETER SECURITY
├─→ API Gateway Rate Limiting
├─→ DDoS Protection
└─→ WAF (Web Application Firewall)

AUTHENTICATION & AUTHORIZATION
├─→ OAuth2 Provider (Keycloak)
├─→ JWT Token Management
├─→ RBAC (Role-Based Access Control)
└─→ Fine-grained permissions

DATA SECURITY
├─→ TLS/mTLS for all traffic
├─→ Encryption at rest
├─→ Encryption in transit
├─→ Vault for secrets management
└─→ Field-level encryption for PII

API SECURITY
├─→ Request validation
├─→ Output encoding
├─→ CORS policies
└─→ API versioning

DATABASE SECURITY
├─→ SQL injection prevention
├─→ Connection pooling with authentication
├─→ Row-level security
└─→ Audit logging

AUDIT & COMPLIANCE
├─→ Immutable audit logs
├─→ Compliance event tracking
├─→ PII handling audit
└─→ Regulatory compliance
```

### 9.2 OAuth2 Flow (Spring Security)

```
USER
    │
    ├─→ Visits Application
    │
    ├─→ Redirects to Keycloak
    │
    ├─→ Keycloak: Authentication & Consent
    │   └─→ Returns Authorization Code
    │
    ├─→ API Gateway exchanges code for JWT
    │
    ├─→ JWT Token contains:
    │   ├─→ User ID
    │   ├─→ Roles
    │   ├─→ Permissions
    │   ├─→ Issued time
    │   └─→ Expiration
    │
    ├─→ All subsequent requests include JWT
    │
    ├─→ API Gateway validates JWT
    │
    └─→ Microservices verify claims via JWT
```

---

## 10. Scalability & Performance

### 10.1 Horizontal Scalability

| Component | Scaling Strategy |
|-----------|---|
| **API Gateway** | Horizontal replicas + Load balancer |
| **Microservices** | Kubernetes auto-scaling (HPA) |
| **PostgreSQL** | Read replicas, connection pooling |
| **Redis** | Cluster mode, cache eviction policies |
| **Kafka** | Partition replication, consumer groups |
| **Vector DB** | Distributed index, sharding |
| **Elasticsearch** | Distributed shards + replicas |

### 10.2 Caching Strategy

```
CACHE LAYERS:
1. Application Cache (Redis) - Hot data, session storage
2. Database Query Cache - Frequently accessed queries
3. Vector DB Cache - Recent embedding searches
4. CDN Cache - Static assets
```

### 10.3 Circuit Breaker Pattern

```
Each microservice implements:
├─→ Hystrix Circuit Breakers
├─→ Timeout policies
├─→ Retry logic with exponential backoff
└─→ Fallback mechanisms
```

---

## 11. Observability & Monitoring

### 11.1 Observability Stack

```
METRICS (Prometheus)
├─→ Application metrics
├─→ JVM metrics
├─→ Business metrics
└─→ AI latency/cost metrics

DISTRIBUTED TRACING (Jaeger)
├─→ Request tracing across services
├─→ Latency analysis
├─→ Error tracking
└─→ AI operation tracing

LOGS (Structured Logging)
├─→ JSON structured logs
├─→ Correlation IDs
├─→ Log aggregation (ELK)
└─→ AI prompt/response logging

VISUALIZATION (Grafana)
├─→ Real-time dashboards
├─→ Service health
├─→ Performance metrics
├─→ AI system metrics
└─→ Business KPIs
```

### 11.2 AI Observability

```
TRACK:
├─→ Prompt execution time
├─→ Token consumption
├─→ AI model latency
├─→ Response quality metrics
├─→ Hallucination detection
├─→ Cost tracking
└─→ Model performance
```

---

## 12. Deployment Architecture

### 12.1 Kubernetes Deployment

```
NAMESPACE: ai-wallet-platform

DEPLOYMENTS:
├─→ api-gateway (replicas: 3)
├─→ identity-service (replicas: 2)
├─→ user-service (replicas: 2)
├─→ wallet-service (replicas: 3)
├─→ transaction-service (replicas: 3)
├─→ payment-service (replicas: 2)
├─→ fraud-detection-service (replicas: 2)
├─→ ai-assistant-service (replicas: 2)
├─→ investigation-service (replicas: 2)
├─→ notification-service (replicas: 2)
├─→ analytics-service (replicas: 2)
├─→ audit-service (replicas: 2)
├─→ config-server (replicas: 1)
└─→ discovery-server (replicas: 1)

STATEFULSETS:
├─→ PostgreSQL (primary + replicas)
├─→ Redis (master + slaves)
├─→ Kafka (brokers)
├─→ Milvus (vector DB)
└─→ Elasticsearch (data nodes)

SERVICES:
├─→ ClusterIP: Internal communication
├─→ NodePort: External exposure
└─→ LoadBalancer: API Gateway

INGRESS:
└─→ API Gateway endpoint
```

### 12.2 Helm Chart Structure

```
ai-wallet-platform/
├─→ Chart.yaml
├─→ values.yaml
├─→ charts/
│   ├─→ api-gateway/
│   ├─→ identity-service/
│   ├─→ user-service/
│   └─→ ... [each service]
└─→ templates/
    ├─→ namespace.yaml
    ├─→ secrets.yaml
    ├─→ configmaps.yaml
    └─→ ingress.yaml
```

---

## 13. Java 21 Modern Features Usage

### 13.1 Virtual Threads (Project Loom)

**Use Case**: API Gateway request handling

```java
// Traditional thread pool exhaustion scenario
// With Virtual Threads: thousands of concurrent requests handled efficiently
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// Each request gets its own virtual thread (very lightweight)
// Ideal for I/O-bound microservices
```

**Rationale**: Microservices are I/O bound (REST calls, DB queries). Virtual threads provide:
- Lightweight concurrency
- Massive scalability without thread pool tuning
- Simplified thread management code
- Native Kotlin coroutine-like benefits

### 13.2 Records (Data Carriers)

**Use Case**: DTOs and Events

```java
// Replaces verbose POJO boilerplate
public record TransactionDTO(
    String id,
    String walletId,
    BigDecimal amount,
    LocalDateTime timestamp
) {}

public record TransactionCreatedEvent(
    String id,
    String walletId,
    BigDecimal amount,
    String initiator
) {}
```

**Rationale**: BFSI systems generate thousands of DTOs. Records:
- Reduce boilerplate by 80%
- Immutable by design (security benefit)
- Better performance (no reflection)
- IDE-friendly equality/hashCode

### 13.3 Pattern Matching (Multi-Release)

**Use Case**: Event type dispatch

```java
// Cleaner event handling
public void processEvent(Event event) {
    match(event) {
        case TransactionCreatedEvent e when e.amount() > 100_000 ->
            handleHighValueTransaction(e);
        case TransactionCreatedEvent e ->
            handleNormalTransaction(e);
        case FraudAlertEvent e ->
            handleFraudAlert(e);
        case _ -> throw new UnknownEventException();
    }
}
```

**Rationale**: Financial systems have complex event types. Pattern matching:
- Eliminates instanceof boilerplate
- Type-safe event handling
- More maintainable

### 13.4 Sealed Classes

**Use Case**: Event hierarchy

```java
public sealed class Event permits
    TransactionCreatedEvent,
    TransactionFailedEvent,
    FraudAlertEvent,
    PaymentSettledEvent {
    // Only defined subtypes are allowed
}
```

**Rationale**: Ensures financial event types are controlled:
- Prevents unauthorized event subclasses
- Compiler exhaustiveness checking
- Self-documenting API contracts

### 13.5 Structured Concurrency (Preview)

**Use Case**: Fraud detection with parallel anomaly checks

```java
// Clean parallel subtask management
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var velocityCheck = scope.fork(() -> checkVelocityAnomaly(tx));
    var geoCheck = scope.fork(() -> checkGeoAnomaly(tx));
    var behaviorCheck = scope.fork(() -> checkBehaviorAnomaly(tx));
    
    scope.joinUntil(Instant.now().plusSeconds(5));
    
    return new FraudAnalysis(
        velocityCheck.resultNow(),
        geoCheck.resultNow(),
        behaviorCheck.resultNow()
    );
}
```

**Rationale**: Fraud detection needs parallel analysis. Structured concurrency:
- Cleaner than CompletableFuture chains
- Automatic resource management
- Better error handling

---

## 14. Non-Functional Requirements Matrix

| NFR | Strategy | Implementation |
|-----|----------|---|
| **Availability** | 99.99% uptime | Multi-zone deployment, health checks, circuit breakers |
| **Scalability** | 10K+ TPS | Kafka partitioning, DB sharding, auto-scaling |
| **Latency** | P99 < 200ms | Caching, async processing, CDN |
| **Throughput** | 100K+ concurrent users | Virtual threads, connection pooling, load balancing |
| **Security** | BFSI grade | OAuth2, encryption, audit logging, zero-trust |
| **Resilience** | Handle cascading failures | Circuit breakers, timeouts, retry policies |
| **Maintainability** | Clean, modular code | DDD, SOLID principles, comprehensive docs |
| **Observability** | Full traceability | Distributed tracing, structured logging, metrics |
| **Cost Optimization** | Efficient resource usage | Auto-scaling, caching, optimized queries |

---

## 15. Implementation Phases

### Phase 1: Foundation
- [ ] Parent Maven setup
- [ ] Database schema & migrations
- [ ] API Gateway
- [ ] Identity Service
- [ ] Kafka infrastructure

### Phase 2: Core Services
- [ ] User Service
- [ ] Wallet Service
- [ ] Transaction Service
- [ ] Audit Service

### Phase 3: Intelligence
- [ ] Fraud Detection Service
- [ ] AI Assistant Service (Spring AI)
- [ ] Investigation Service (RAG)

### Phase 4: Ecosystem
- [ ] Payment Service
- [ ] Notification Service
- [ ] Analytics Service

### Phase 5: Operations
- [ ] Deployment (K8s + Helm)
- [ ] Observability (OpenTelemetry)
- [ ] Testing & Documentation
- [ ] React Frontend

---

## 16. Technology Justification

| Technology | Why BFSI? |
|-----------|----------|
| **Java 21** | Enterprise standard, long-term support, performance |
| **Spring Boot 3.x** | Mature ecosystem, cloud-native design |
| **PostgreSQL** | ACID compliance, reliability, audit-trail support |
| **Redis** | High-performance caching, session management |
| **Kafka** | Event sourcing, immutable transaction log |
| **Spring AI** | Native LLM integration, RAG support, cost-effective |
| **Kubernetes** | Resilience, scalability, multi-region deployment |
| **React** | Modern UX, real-time updates |

---

This architecture is production-ready and demonstrates enterprise-scale fintech engineering.
