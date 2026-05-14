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

## Phase 5: Uplift Modeling / Targeting
1. Criteo dataset experiment notebook
2. Train T-Learner uplift model
3. Evaluate Qini/AUUC/Uplift@K
4. Export customer uplift score
5. targeting schema
6. targeting module
7. API get target customers
8. CampaignActivatedEvent -> TargetCustomersSelectedEvent

## Phase 6: Production
1. Security + JWT
2. Redis
3. Scheduler
4. Kafka externalization
5. Monitoring
6. CI/CD
7. Deployment