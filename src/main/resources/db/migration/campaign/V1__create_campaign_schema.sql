CREATE SCHEMA IF NOT EXISTS campaign;

CREATE TABLE IF NOT EXISTS campaign.campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    budget_amount DECIMAL(15, 2),
    budget_currency VARCHAR(3) DEFAULT 'VND',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_campaigns_status ON campaign.campaigns(status);
CREATE INDEX IF NOT EXISTS idx_campaigns_dates ON campaign.campaigns(start_date, end_date);