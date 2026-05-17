# Database trong Autolift

## 1. Database Ownership

- Một PostgreSQL database: `autolift_db`
- Mỗi module sở hữu một schema riêng

| Module | Schema |
|--------|--------|
| sandbox | sandbox |
| campaign | campaign |
| promotion | promotion |
| customer | customer |
| voucher | voucher |
| loyalty | loyalty |
| redemption | redemption |
| targeting | targeting |
| explainability | explainability |
| notification | notification |

## 2. Cross-Schema Rules

**Được phép:**
- voucher publish VoucherRedeemedEvent -> loyalty listen
- targeting đọc score từ targeting.customer_uplift_scores
- explainability đọc gp_rules từ explainability.gp_rules

**Không được phép:**
- campaign gọi trực tiếp LoyaltyRepository
- voucher update trực tiếp loyalty.point_transactions
- targeting update trực tiếp campaign.campaigns
- explainability update trực tiếp targeting.customer_uplift_scores

## 3. Flyway Migration

### Cấu trúc migration
```
src/main/resources/db/migration/
├── __root/                    # migrations chung (nếu cần)
│   └── V1__init.sql
└── sandbox/                   # migrations cho module sandbox
    └── V1__create_table.sql
```

### Naming Convention
```
V1__create_table.sql
V2__add_column.sql
```

## 4. Configuration

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    properties:
      hibernate:
        default_schema: sandbox
```

## 5. Ví dụ Migration

### sandbox schema
```sql
CREATE SCHEMA IF NOT EXISTS sandbox;

CREATE TABLE IF NOT EXISTS sandbox.sandboxes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### targeting schema
```sql
CREATE SCHEMA IF NOT EXISTS targeting;

CREATE TABLE targeting.customer_uplift_scores (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    campaign_id UUID,
    uplift_score NUMERIC(10, 6) NOT NULL,
    treatment_probability NUMERIC(10, 6),
    control_probability NUMERIC(10, 6),
    segment VARCHAR(50) NOT NULL,
    model_version VARCHAR(100) NOT NULL,
    scored_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_customer_uplift_scores_customer
ON targeting.customer_uplift_scores(customer_id);

CREATE INDEX idx_customer_uplift_scores_campaign_score
ON targeting.customer_uplift_scores(campaign_id, uplift_score DESC);
```

### targeting schema - customer_feature_snapshots
```sql
CREATE TABLE targeting.customer_feature_snapshots (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
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
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_customer_feature_snapshots_customer
ON targeting.customer_feature_snapshots(customer_id);
```

### explainability schema
```sql
CREATE SCHEMA IF NOT EXISTS explainability;

CREATE TABLE explainability.gp_rules (
    id UUID PRIMARY KEY,
    campaign_id UUID,
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
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_gp_rules_campaign_f1
ON explainability.gp_rules(campaign_id, f1_score DESC);

CREATE TABLE explainability.gp_xor_demo_runs (
    id UUID PRIMARY KEY,
    run_name VARCHAR(255) NOT NULL,
    best_rule_text TEXT NOT NULL,
    best_fitness NUMERIC(10, 6),
    generation_count INTEGER,
    population_size INTEGER,
    mutation_rate NUMERIC(10, 6),
    crossover_rate NUMERIC(10, 6),
    created_at TIMESTAMP NOT NULL
);
```