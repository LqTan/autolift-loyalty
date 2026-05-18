# Implementation Order

## Phase 1: Foundation
1. Root Spring Boot app
2. Spring Modulith
3. PostgreSQL Docker Compose
4. Flyway
5. Sandbox module
6. Modulith verification test

## Phase 2: Campaign + Promotion
1. Campaign domain
2. Promotion domain
3. Create campaign
4. Activate campaign
5. Create promotion rule

## Phase 3: Customer + Voucher
1. Customer domain
2. Voucher domain
3. Issue voucher
4. Redeem voucher

## Phase 4: Loyalty
1. Loyalty account
2. Point transaction
3. Add points
4. Use points
5. Listen VoucherRedeemedEvent

## Phase 5: Uplift Modeling / Targeting với X5 RetailHero
1. X5 RetailHero experiment notebook
2. Build X5 feature engineering pipeline
3. Train T-Learner uplift model
4. Evaluate Qini/AUUC/Uplift@K
5. Export customer_uplift_scores.csv
6. Export customer_feature_snapshots.csv
7. targeting schema
8. targeting module
9. API get target customers
10. CampaignActivatedEvent -> TargetCustomersSelectedEvent
11. ML job queue (ml schema + ml module)

## Phase 6: Genetic Programming / Explainability
1. Build gp_input.csv từ uplift score và feature snapshots
2. GP rule extraction trên dữ liệu X5 (XOR demo tách riêng nếu cần cho môn Các hệ cơ sở tri thức)
3. Export gp_rules.csv
4. explainability schema
5. explainability module
6. API get GP rules by campaign
7. Python worker cho ML job queue

## Phase 7: Production
1. Security + JWT
2. Redis
3. Scheduler
4. Monitoring
5. CI/CD
6. Deployment
7. ML service riêng nếu cần online scoring về sau

## Phase 8: Dashboard
1. Dashboard REST API endpoints
2. Frontend dashboard UI hiển thị:
   - Campaign metrics
   - Uplift targeting results
   - GP rules và metrics
   - Loyalty points summary