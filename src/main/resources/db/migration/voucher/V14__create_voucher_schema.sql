CREATE SCHEMA IF NOT EXISTS voucher;

CREATE TABLE IF NOT EXISTS voucher.vouchers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL UNIQUE,
    campaign_id VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    value DECIMAL(15,2) NOT NULL,
    min_order_amount DECIMAL(15,2),
    max_usage INTEGER,
    used_count INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    valid_from TIMESTAMP,
    valid_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vouchers_code ON voucher.vouchers(code);
CREATE INDEX IF NOT EXISTS idx_vouchers_campaign_id ON voucher.vouchers(campaign_id);
CREATE INDEX IF NOT EXISTS idx_vouchers_status ON voucher.vouchers(status);