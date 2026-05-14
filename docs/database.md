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
| notification | notification |

## 2. Cross-Schema Rules

**Được phép:**
- voucher publish VoucherRedeemedEvent -> loyalty listen
- targeting đọc score từ targeting.customer_uplift_scores

**Không được phép:**
- campaign gọi trực tiếp LoyaltyRepository
- voucher update trực tiếp loyalty.point_transactions
- targeting update trực tiếp campaign.campaigns

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