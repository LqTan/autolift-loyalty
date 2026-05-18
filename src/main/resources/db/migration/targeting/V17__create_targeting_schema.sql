CREATE SCHEMA IF NOT EXISTS targeting;

CREATE TABLE targeting.customer_uplift_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id VARCHAR(255) NOT NULL,
    campaign_id UUID,
    uplift_score NUMERIC(10, 6) NOT NULL,
    treatment_probability NUMERIC(10, 6),
    control_probability NUMERIC(10, 6),
    segment VARCHAR(50) NOT NULL,
    model_version VARCHAR(100) NOT NULL,
    scored_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(customer_id, campaign_id)
);

CREATE TABLE targeting.customer_feature_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id VARCHAR(255) NOT NULL,
    campaign_id UUID,
    recency_days INTEGER,
    frequency_90d INTEGER,
    monetary_90d NUMERIC(14, 2),
    avg_basket_value NUMERIC(14, 2),
    total_quantity_90d NUMERIC(14, 2),
    unique_product_count INTEGER,
    unique_category_count INTEGER,
    favorite_category VARCHAR(100),
    feature_version VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_uplift_scores_customer ON targeting.customer_uplift_scores(customer_id);
CREATE INDEX idx_customer_uplift_scores_campaign_score ON targeting.customer_uplift_scores(campaign_id, uplift_score DESC);
CREATE INDEX idx_customer_feature_snapshots_customer ON targeting.customer_feature_snapshots(customer_id);