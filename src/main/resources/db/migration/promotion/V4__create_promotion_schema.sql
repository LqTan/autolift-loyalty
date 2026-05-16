CREATE SCHEMA IF NOT EXISTS promotion;

CREATE TABLE IF NOT EXISTS promotion.promotions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    promotion_type VARCHAR(50) NOT NULL,
    value DECIMAL(15,2) NOT NULL,
    min_order_amount DECIMAL(15,2),
    applicable_customer_segment VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_promotions_status ON promotion.promotions(status);
CREATE INDEX IF NOT EXISTS idx_promotions_type ON promotion.promotions(promotion_type);
CREATE INDEX IF NOT EXISTS idx_promotions_dates ON promotion.promotions(start_date, end_date);