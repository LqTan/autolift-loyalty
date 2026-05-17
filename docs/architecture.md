# Autolift Loyalty - Architecture

## 1. Mục tiêu tài liệu

Project `autolift-loyalty` theo hướng:
- Spring Boot 3.x + Java 21
- Spring Modulith (modular monolith)
- Domain-Driven Design + CQRS
- PostgreSQL schema per module
- Flyway + JPA/Hibernate
- Uplift Modeling cho môn Khai phá dữ liệu

## 2. Kiến trúc tổng thể

```
src/main/java/com/autolift/
├── AutoliftApplication.java
├── sandbox/           # Test module (technical foundation)
├── campaign/          # Quản lý chiến dịch
├── promotion/        # Quản lý rule ưu đãi
├── customer/         # Quản lý khách hàng
├── voucher/          # Phát hành & redeem voucher
├── loyalty/          # Quản lý điểm thưởng
├── redemption/       # Ghi nhận redeem
├── targeting/       # Chọn khách hàng mục tiêu (Uplift Modeling)
├── explainability/  # GP rule diễn giải nhóm khách hàng
├── notification/    # Gửi thông báo
└── shared/          # Shared kernel tối thiểu
```

## 3. Modules

| Module | Mô tả |
|--------|-------|
| sandbox | Module test kỹ thuật |
| campaign | Quản lý chiến dịch |
| promotion | Quản lý rule ưu đãi |
| customer | Quản lý khách hàng |
| voucher | Phát hành & redeem voucher |
| loyalty | Quản lý điểm thưởng |
| redemption | Ghi nhận redeem |
| targeting | Chọn khách hàng mục tiêu (Uplift Modeling) |
| explainability | GP rule diễn giải nhóm khách hàng |
| notification | Gửi thông báo |
| shared | Shared kernel tối thiểu |

## 4. Module Structure (DDD + CQRS)

```
module-name/
├── api/              # REST controllers, DTOs
├── application/      # Command/query handlers
├── domain/           # Entities, value objects, repositories
├── events/           # Domain events
└── infrastructure/  # JPA, persistence adapters
```

## 5. Module Dependencies Rule

```
api -> application -> domain
infrastructure -> domain
module A -> module B events only
```

**Không cho:**
- api -> infrastructure
- domain -> infrastructure
- domain -> JPA/Spring
- module A -> module B application/domain/infrastructure

## 6. Cấu trúc repository đầy đủ

```
autolift-loyalty
├── pom.xml
├── compose.yaml
├── src/main/java/com/autolift/
│   └── [modules]
├── src/main/resources/db/migration/
├── ml/                    # Python ML project (tách biệt)
│   ├── notebooks/
│   ├── src/
│   ├── models/
│   └── outputs/
└── docs/
```

## 7. Event Flow giữa modules

```
CampaignActivatedEvent
    -> targeting listen
    -> targeting chọn khách hàng Persuadable
    -> TargetCustomersSelectedEvent
    -> voucher/promotion phát voucher hoặc gán offer
```

## 8. Application Module Marker

`AutoliftApplication.java` đặt ở package gốc:

```java
package com.autolift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutoliftApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoliftApplication.class, args);
    }
}
```

Spring Modulith tự nhận các package con trực tiếp dưới `com.autolift` là application modules.