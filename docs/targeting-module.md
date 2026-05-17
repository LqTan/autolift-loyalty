# Targeting Module - Uplift Modeling

Module `targeting` dùng để xác định nhóm khách hàng `Persuadable`.

## 1. Tại sao module riêng?

- `promotion` chỉ quản lý rule ưu đãi
- `campaign` chỉ quản lý chiến dịch
- `targeting` quyết định khách hàng nào nên nhận ưu đãi
- Targeting có thể dùng: rule-based, segment, uplift modeling, manual upload

## 2. Cấu trúc module

```
targeting
├── package-info.java
├── api
│   ├── command
│   │   ├── ImportUpliftScoreController.java
│   │   └── RefreshUpliftScoreController.java
│   └── query
│       ├── TargetingQueryController.java
│       ├── FeatureSnapshotQueryController.java
│       └── TargetCustomerResponse.java
├── application
│   ├── command
│   │   ├── ImportUpliftScoresCommand.java
│   │   ├── ImportUpliftScoresHandler.java
│   │   ├── ImportFeatureSnapshotsCommand.java
│   │   ├── ImportFeatureSnapshotsHandler.java
│   │   ├── RefreshCampaignTargetingCommand.java
│   │   └── RefreshCampaignTargetingHandler.java
│   ├── query
│   │   ├── GetTargetCustomersQuery.java
│   │   ├── GetTargetCustomersHandler.java
│   │   ├── GetCustomerFeatureSnapshotQuery.java
│   │   └── TargetCustomerView.java
│   └── service
│       └── TargetingDecisionService.java
├── domain
│   ├── model
│   │   ├── CustomerUpliftScore.java
│   │   ├── CustomerFeatureSnapshot.java
│   │   ├── TargetingSegment.java
│   │   └── TargetingDecision.java
│   ├── valueobject
│   │   ├── UpliftScore.java
│   │   ├── ModelVersion.java
│   │   ├── ScoreTimestamp.java
│   │   ├── RecencyDays.java
│   │   ├── Frequency.java
│   │   └── MonetaryValue.java
│   ├── repository
│   │   ├── CustomerUpliftScoreRepository.java
│   │   └── CustomerFeatureSnapshotRepository.java
│   └── service
│       └── PersuadableSegmentationPolicy.java
├── events
│   ├── package-info.java
│   └── TargetCustomersSelectedEvent.java
└── infrastructure
    ├── persistence
    │   ├── entity
    │   │   ├── CustomerUpliftScoreJpaEntity.java
    │   │   └── CustomerFeatureSnapshotJpaEntity.java
    │   ├── mapper
    │   │   ├── CustomerUpliftScoreMapper.java
    │   │   └── CustomerFeatureSnapshotMapper.java
    │   ├── repository
    │   │   ├── CustomerUpliftScoreJpaRepository.java
    │   │   ├── CustomerFeatureSnapshotJpaRepository.java
    │   │   ├── CustomerUpliftScoreRepositoryAdapter.java
    │   │   └── CustomerFeatureSnapshotRepositoryAdapter.java
    │   └── readmodel
    │       └── TargetingReadRepository.java
    └── importfile
        ├── UpliftScoreCsvImporter.java
        └── CustomerFeatureSnapshotCsvImporter.java
```

## 3. API

### Query: Get target customers
```
GET /api/targeting/campaigns/{campaignId}/candidates?limit=1000
```

Response:
```json
[
  {
    "customerId": "8e64...",
    "upliftScore": 0.231,
    "segment": "PERSUADABLE"
  }
]
```

## 4. TargetingSegment Enum

```java
public enum TargetingSegment {
    PERSUADABLE,   // uplift_score >= 0.05
    NEUTRAL,       // -0.01 < uplift_score < 0.05
    DO_NOT_TARGET; // uplift_score <= -0.01

    public static TargetingSegment from(UpliftScore score) { ... }
}
```