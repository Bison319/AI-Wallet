-- Initialize AI Wallet Platform Databases

-- Create databases
CREATE DATABASE ai_wallet_identity;
CREATE DATABASE ai_wallet_user;
CREATE DATABASE ai_wallet_wallet;
CREATE DATABASE ai_wallet_transaction;
CREATE DATABASE ai_wallet_payment;
CREATE DATABASE ai_wallet_fraud;
CREATE DATABASE ai_wallet_assistant;
CREATE DATABASE ai_wallet_investigation;
CREATE DATABASE ai_wallet_notification;
CREATE DATABASE ai_wallet_analytics;
CREATE DATABASE ai_wallet_audit;

-- Connect to each database and create extensions

-- Identity Database
\c ai_wallet_identity
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- User Database
\c ai_wallet_user
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Wallet Database
\c ai_wallet_wallet
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Transaction Database
\c ai_wallet_transaction
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Payment Database
\c ai_wallet_payment
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Fraud Database
\c ai_wallet_fraud
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Assistant Database
\c ai_wallet_assistant
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Investigation Database
\c ai_wallet_investigation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- Add pgvector extension for embeddings
CREATE EXTENSION IF NOT EXISTS "vector";

-- Notification Database
\c ai_wallet_notification
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Analytics Database
\c ai_wallet_analytics
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Audit Database
\c ai_wallet_audit
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
-- Audit logs are immutable - create append-only table
CREATE EXTENSION IF NOT EXISTS "amcheck";

-- Create initial schemas for each service

-- Identity Service tables
\c ai_wallet_identity

CREATE TABLE IF NOT EXISTS user_auth (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    role VARCHAR(50) DEFAULT 'USER',
    account_status VARCHAR(50) DEFAULT 'ACTIVE',
    enabled BOOLEAN DEFAULT true,
    credentials_not_expired BOOLEAN DEFAULT true,
    account_not_locked BOOLEAN DEFAULT true,
    last_login_at TIMESTAMP,
    password_changed_at TIMESTAMP,
    failed_login_attempts INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_user_auth_email ON user_auth(email);
CREATE INDEX idx_user_auth_phone ON user_auth(phone);

-- Common tables (replicated across all databases for reference)

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    identity_type VARCHAR(50) NOT NULL,
    identity_number VARCHAR(255) NOT NULL UNIQUE,
    account_status VARCHAR(50) DEFAULT 'ACTIVE',
    kyc_completed BOOLEAN DEFAULT false,
    aml_verified BOOLEAN DEFAULT false,
    kyc_data TEXT,
    risk_profile VARCHAR(50),
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) DEFAULT 'SYSTEM',
    deleted BOOLEAN DEFAULT false,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_phone ON users(phone);

CREATE TABLE IF NOT EXISTS wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    wallet_type VARCHAR(50) NOT NULL,
    wallet_name VARCHAR(255) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    ledger_balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    daily_limit NUMERIC(19,2),
    monthly_limit NUMERIC(19,2),
    daily_spent NUMERIC(19,2) DEFAULT 0,
    monthly_spent NUMERIC(19,2) DEFAULT 0,
    failed_attempts INT DEFAULT 0,
    verified BOOLEAN DEFAULT false,
    account_number VARCHAR(20),
    ifsc_code VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) DEFAULT 'SYSTEM',
    deleted BOOLEAN DEFAULT false,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_wallet_user_id ON wallets(user_id);
CREATE INDEX idx_wallet_type ON wallets(wallet_type);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    fee NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    description TEXT,
    merchant_id VARCHAR(255),
    merchant_name VARCHAR(255),
    merchant_category VARCHAR(100),
    payment_method VARCHAR(50),
    reference_number VARCHAR(255),
    correlation_id VARCHAR(255),
    risk_score VARCHAR(50),
    fraud_checked BOOLEAN DEFAULT false,
    suspicious BOOLEAN DEFAULT false,
    device_location VARCHAR(255),
    transaction_location VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'SYSTEM',
    updated_by VARCHAR(255) DEFAULT 'SYSTEM',
    deleted BOOLEAN DEFAULT false,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_transaction_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transaction_status ON transactions(status);
CREATE INDEX idx_transaction_created_at ON transactions(created_at);
CREATE INDEX idx_transaction_merchant ON transactions(merchant_id);

-- Grant permissions (adjust as needed for production)
GRANT ALL PRIVILEGES ON ALL DATABASES TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;

-- Display initialization summary
\echo '================================'
\echo 'AI Wallet Platform Database Setup'
\echo '================================'
\echo 'Databases created:'
\echo '  - ai_wallet_identity'
\echo '  - ai_wallet_user'
\echo '  - ai_wallet_wallet'
\echo '  - ai_wallet_transaction'
\echo '  - ai_wallet_payment'
\echo '  - ai_wallet_fraud'
\echo '  - ai_wallet_assistant'
\echo '  - ai_wallet_investigation'
\echo '  - ai_wallet_notification'
\echo '  - ai_wallet_analytics'
\echo '  - ai_wallet_audit'
\echo ''
\echo 'Extensions installed:'
\echo '  - uuid-ossp'
\echo '  - pgcrypto'
\echo '  - vector (for embeddings in investigation DB)'
\echo ''
\echo 'Tables created:'
\echo '  - user_auth (Identity Service)'
\echo '  - users'
\echo '  - wallets'
\echo '  - transactions'
\echo ''
\echo 'Ready for application deployment!'
\echo '================================'
