# DDD + CQRS trong Autolift

## 1. DDD Concepts

### Khái niệm sử dụng

| Khái niệm | Mô tả |
|-----------|-------|
| Bounded Context | Ranh giới nghiệp vụ, tương ứng với module như campaign, voucher, loyalty, targeting, explainability |
| Aggregate | Cụm object có consistency boundary, ví dụ Campaign, Voucher, LoyaltyAccount, CustomerUpliftScore, GpRule |
| Aggregate Root | Object gốc được thao tác từ bên ngoài aggregate |
| Entity | Object có identity riêng (Campaign, Voucher, Customer, GpRule) |
| Value Object | Object không có identity, so sánh bằng value (CampaignId, VoucherCode, PointAmount, UpliftScore, RuleExpression) |
| Domain Event | Sự kiện nghiệp vụ đã xảy ra (CampaignActivatedEvent, VoucherRedeemedEvent, TargetCustomersSelectedEvent, GpRulesImportedEvent) |
| Repository Port | Interface cho domain/application làm việc với persistence |
| Domain Service | Rule nghiệp vụ không thuộc về một aggregate cụ thể |

### Rule

- Business rule nằm trong domain model hoặc domain service
- Application handler chỉ điều phối use case
- Infrastructure không leak vào domain
- Module khác không gọi domain nội bộ của module này

## 2. Domain Structure

```
module-name/domain/
├── model/            # Entities
├── valueobject/      # Value objects (e.g., SandboxId as @Embeddable)
├── repository/       # Repository ports
└── exception/        # Domain exceptions
```

## 3. CQRS Pattern

### Command Side (Write)
- Tạo dữ liệu
- Thay đổi trạng thái
- Chạy business rule
- Đi qua aggregate/domain model
- Có transaction
- Có thể publish event

### Query Side (Read)
- Chỉ đọc dữ liệu
- Không thay đổi trạng thái
- Trả DTO/view model
- Có thể dùng projection/read repository
- Không nhất thiết dựng aggregate

### API Examples

```
Command:
POST /api/campaigns
POST /api/campaigns/{id}/activate
POST /api/vouchers/{code}/redeem
POST /api/loyalty/accounts/{id}/points
POST /api/targeting/scores/import
POST /api/explainability/gp-rules/import

Query:
GET /api/campaigns/{id}
GET /api/vouchers/{code}
GET /api/customers/{id}/points
GET /api/targeting/campaigns/{id}/candidates
GET /api/explainability/campaigns/{id}/rules
GET /api/explainability/xor-demo/runs/{id}
```