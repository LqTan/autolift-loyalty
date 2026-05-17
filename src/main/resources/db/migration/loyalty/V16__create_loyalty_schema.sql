CREATE SCHEMA IF NOT EXISTS loyalty;

CREATE TABLE loyalty.loyalty_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id VARCHAR(255) NOT NULL UNIQUE,
    points_balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    tier VARCHAR(50) NOT NULL DEFAULT 'BRONZE',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE loyalty.point_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    loyalty_account_id UUID NOT NULL REFERENCES loyalty.loyalty_accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    reference_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_accounts_customer ON loyalty.loyalty_accounts(customer_id);
CREATE INDEX idx_loyalty_accounts_status ON loyalty.loyalty_accounts(status);
CREATE INDEX idx_point_transactions_account ON loyalty.point_transactions(loyalty_account_id);