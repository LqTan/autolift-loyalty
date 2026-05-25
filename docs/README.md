# Autolift Loyalty

## Project Overview

Customer loyalty and rewards system built with Java 21 + Spring Boot 3.5, following a modular monolith architecture with Domain-Driven Design and CQRS patterns.

## Tech Stack

- Java 21 + Spring Boot 3.5
- Spring Modulith 1.4 (modular monolith)
- PostgreSQL (schema per module)
- Flyway migrations
- JPA/Hibernate
- Lombok

## Modules

| Module | Purpose |
|--------|---------|
| `sandbox` | Technical foundation and verification |
| `campaign` | Campaign management |
| `promotion` | Promotion rules |
| `customer` | Customer management |
| `voucher` | Voucher issuance & redemption |
| `loyalty` | Points & rewards management |
| `redemption` | Redemption tracking |
| `targeting` | Uplift scores storage |
| `ml` | ML job orchestration |
| `notification` | Notifications |

## Architecture

```
src/main/java/com/autolift/
├── AutoliftApplication.java
├── sandbox/           # Technical foundation
├── campaign/          # Campaign management
├── promotion/         # Promotion rules
├── customer/          # Customer management
├── voucher/           # Voucher issuance & redemption
├── loyalty/           # Points & rewards
├── redemption/        # Redemption tracking
├── targeting/         # Uplift scores
├── ml/                # ML orchestration
├── notification/      # Notifications
└── shared/            # Shared kernel (minimal)
```

### Module Structure (DDD + CQRS)

```
module-name/
├── api/              # REST controllers, DTOs
├── application/      # Command/query handlers
├── domain/           # Entities, value objects, repositories
├── events/           # Domain events
└── infrastructure/  # JPA, persistence adapters
```

### Module Dependency Rules

```
✓ api -> application -> domain
✓ infrastructure -> domain
✓ module A -> module B events only

✗ api -> infrastructure
✗ domain -> infrastructure
✗ domain -> JPA/Spring
✗ module A -> module B domain/application/infrastructure
```

## Business Domains

### Campaign Management
Manage promotional campaigns: create, activate, pause, expire. Control timing and budget.

### Promotion Management
Define promotion rules: percentage discount, fixed amount, minimum order conditions, customer group targeting.

### Voucher Management
Issue vouchers, lock/unlock vouchers, redeem vouchers, check expiry and redemption count.

### Loyalty Management
Manage point accounts: add points, deduct points, point transaction history, point expiration.

### Targeting / Uplift Modeling
Calculate uplift scores to identify `Persuadable` customers - those likely to change purchase behavior when receiving promotions.

### Notification
Send notifications on events: campaign activated, voucher redeemed, points added, target customers selected.

## Database

### Schema Per Module

| Module | Schema |
|--------|--------|
| sandbox | sandbox |
| campaign | campaign |
| promotion | promotion |
| customer | customer |
| voucher | voucher |
| loyalty | loyalty |
| targeting | targeting |
| ml | ml |

### Flyway Migration Structure

```
src/main/resources/db/migration/
├── sandbox/
│   └── V11__create_sandbox_schema.sql
├── campaign/
│   └── V12__create_campaign_schema.sql
└── [other modules]/
```

Naming: `V{migration_number}{module_id}__{description}.sql`

- Digit 1 = migration sequence (1, 2, 3...)
- Digit 2 = module ID:
  - 1 = sandbox
  - 2 = campaign
  - 3 = customer
  - 4 = voucher
  - 5 = promotion

Example: `V21` = migration 1 of campaign

### Cross-Schema Rules

**Allowed:**
- voucher publishes `VoucherRedeemedEvent` -> loyalty listens
- targeting reads scores from `targeting.customer_uplift_scores`

**Not allowed:**
- campaign directly calls LoyaltyRepository
- voucher directly updates `loyalty.point_transactions`

## Uplift Modeling

### Problem Statement

Unlike standard conversion prediction ("Will the customer buy?"), uplift modeling answers: "Will the promotion cause the customer to change behavior?"

```
uplift(x) = P(Y = 1 | X = x, T = 1) - P(Y = 1 | X = x, T = 0)
```

Where:
- X = customer features
- T = treatment (received promotion or not)
- Y = outcome (purchased or not)

### Uplift Score Interpretation

| Uplift Score | Segment | Action |
|-------------|---------|--------|
| > 0.05 | Persuadable | Prioritize for promotions |
| -0.01 to 0.05 | Neutral | Uncertain |
| < -0.01 | Do-not-target | Not worth targeting |

### Approach: T-Learner (Two-Model)

1. Train model_treatment on customers who received treatment
2. Train model_control on customers who did not receive treatment
3. For each customer: `uplift_score = p_treatment - p_control`
4. Rank customers by uplift_score descending

### Evaluation Metrics

- **Uplift@K**: Uplift when targeting top K% customers
- **Qini Curve**: Incremental gain curve
- **AUUC**: Area Under Uplift Curve

### ML Pipeline: Python -> Spring Boot

```
1. Load X5 RetailHero Dataset
2. Build behavioral features from purchase history
3. Train model_treatment and model_control
4. Calculate uplift_scores
5. Export customer_uplift_scores.csv
6. Import CSV into PostgreSQL targeting.customer_uplift_scores
7. Spring Boot targeting API serves scores
```

### Python ML Structure

```
ml/
├── notebooks/
├── src/
│   ├── build_x5_features.py
│   ├── train_uplift_model.py
│   └── batch_score_customers.py
├── models/
└── outputs/
    └── customer_uplift_scores.csv
```

## Running Local

```bash
./scripts/compile.sh       # Compile
./scripts/migrate.sh       # Migrate database
./scripts/run.sh           # Run app (port 8080)
./scripts/test.sh          # Run tests
./scripts/clean-migrate.sh  # Clean + migrate (reset DB)
```

## API Documentation

After running the app: `GET /swagger-ui.html`

## DDD Concepts

| Concept | Description |
|---------|-------------|
| Bounded Context | Business boundary, corresponds to module |
| Aggregate | Group of objects with consistency boundary |
| Aggregate Root | Primary object accessed from outside aggregate |
| Entity | Object with own identity |
| Value Object | Object compared by value, no identity |
| Domain Event | Business event (CampaignActivatedEvent, etc.) |
| Repository Port | Interface for domain to work with persistence |

### CQRS Pattern

**Command (Write):**
- Create/modify data
- Run business rules
- Go through aggregate/domain model
- Have transaction
- May publish event

**Query (Read):**
- Read-only data access
- Return DTO/view model
- No state modification

## References

- [Spring Modulith Reference](https://docs.spring.io/spring-modulith/reference/fundamentals.html)
- [Spring Modulith Events](https://docs.spring.io/spring-modulith/reference/events.html)
- [X5 RetailHero Uplift Modeling](https://ods.ai/competitions/x5-retailhero-uplift-modeling)
- [scikit-uplift Documentation](https://www.uplift-modeling.com/en/latest/index.html)
- [EconML Meta-Learners](https://www.pywhy.org/EconML/spec/estimation/metalearners.html)
- [Martin Fowler - CQRS](https://martinfowler.com/bliki/CQRS.html)