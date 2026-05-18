CREATE SCHEMA IF NOT EXISTS explainability;

CREATE TABLE explainability.gp_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id VARCHAR(255),
    rule_text TEXT NOT NULL,
    rule_expression TEXT NOT NULL,
    target_label VARCHAR(100) NOT NULL,
    precision_value NUMERIC(10, 6),
    recall_value NUMERIC(10, 6),
    f1_score NUMERIC(10, 6),
    accuracy_value NUMERIC(10, 6),
    coverage_value NUMERIC(10, 6),
    model_version VARCHAR(100) NOT NULL,
    source_file VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(campaign_id, rule_text)
);

CREATE INDEX idx_gp_rules_campaign_f1
ON explainability.gp_rules(campaign_id, f1_score DESC);

CREATE INDEX idx_gp_rules_campaign_model
ON explainability.gp_rules(campaign_id, model_version);