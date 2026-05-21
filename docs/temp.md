# ML Jobs Flow - FE Integration

## Flow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          NEW AUTOMATED FLOW                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. Frontend: POST /api/ml/jobs (trigger ML)                                │
│                    ↓                                                         │
│  2. Backend: Job created → immediately COMPLETED (mock)                     │
│                    ↓                                                         │
│  3. Backend: publishes UpliftScoresImportRequestedEvent                     │
│                    ↓                                                         │
│  4. Backend: UpliftScoreImportListener auto-imports CSV → DB                 │
│                    ↓                                                         │
│  5. Frontend: poll GET /api/ml/jobs/{id} → COMPLETED                         │
│                    ↓                                                         │
│  6. Frontend: GET /api/targeting/campaigns/{id}/candidates → scores ready    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 1. Tạo Campaign

```bash
POST /api/campaigns/generate-test
Body: { "count": 3 }
```

**Response 200:**
```json
{
  "generated": 3,
  "campaigns": [
    { "id": "abc-123", "name": "Summer Sale abc-123", "status": "CREATED" }
  ]
}
```

## 2. Trigger Uplift Scoring Job

```bash
POST /api/ml/jobs
Body: {
  "jobType": "UPLIFT_SCORING",
  "campaignId": "abc-123",
  "modelVersion": "v1",
  "inputParams": {
    "top_k_rate": 0.2,
    "n_estimators": 100,
    "base_model": "RandomForestClassifier"
  }
}
```

**Response 200:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "jobType": "UPLIFT_SCORING",
  "campaignId": "abc-123",
  "status": "COMPLETED",
  "modelVersion": "v1",
  "resultPath": "ml/artifacts/outputs/customer_uplift_scores.csv",
  "createdAt": "2026-05-21T09:00:00Z",
  "startedAt": "2026-05-21T09:00:00Z",
  "completedAt": "2026-05-21T09:00:01Z"
}
```

**Backend会自动:**
1. 创建 Job record (status=PENDING → RUNNING → COMPLETED)
2. 发布 `UpliftScoresImportRequestedEvent`
3. `UpliftScoreImportListener` 读取 CSV 并导入 `targeting.customer_uplift_scores` 表

## 3. Poll Job Status (Optional - vì job đã COMPLETED ngay)

```bash
GET /api/ml/jobs/550e8400-e29b-41d4-a716-446655440000
```

**Response 200:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "jobType": "UPLIFT_SCORING",
  "campaignId": "abc-123",
  "status": "COMPLETED",
  "modelVersion": "v1",
  "resultPath": "ml/artifacts/outputs/customer_uplift_scores.csv",
  "createdAt": "2026-05-21T09:00:00Z",
  "startedAt": "2026-05-21T09:00:00Z",
  "completedAt": "2026-05-21T09:00:01Z"
}
```

## 4. Query Targeting Candidates

```bash
GET /api/targeting/campaigns/abc-123/candidates?limit=100
```

**Response 200:**
```json
[
  {
    "customerId": "8e64b4e7-...",
    "upliftScore": 0.74,
    "segment": "PERSUADABLE"
  },
  {
    "customerId": "9f75c5f8-...",
    "upliftScore": 0.72,
    "segment": "PERSUADABLE"
  }
]
```

## 5. JS Integration (Simplified)

```javascript
async function triggerUpliftScoring(campaignId) {
  // Bước 1: Trigger job (COMPLETED ngay lập tức + auto-import)
  const response = await fetch('/api/ml/jobs', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      jobType: 'UPLIFT_SCORING',
      campaignId: campaignId,
      modelVersion: 'v1',
      inputParams: { top_k_rate: 0.2 }
    })
  });
  const job = await response.json();

  // Job đã COMPLETED, scores đã import sẵn
  // Có thể query candidates ngay
  const candidates = await fetch(`/api/targeting/campaigns/${campaignId}/candidates?limit=100`);
  return candidates.json();
}
```

## 6. Trigger GP Rules Extraction

```bash
POST /api/ml/jobs
Body: {
  "jobType": "GP_RULE_EXTRACTION",
  "campaignId": "abc-123",
  "modelVersion": "v1",
  "upliftScoreJobId": "550e8400-e29b-41d4-a716-446655440000",
  "inputParams": {
    "top_k_rate": 0.2,
    "population_size": 200,
    "generations": 20
  }
}
```

## 7. Query GP Rules

```bash
GET /api/explainability/campaigns/abc-123/gp-rules
```

**Response 200:**
```json
[
  {
    "id": 1,
    "campaignId": "abc-123",
    "ruleText": "(frequency_90d > 5) AND (recency_days <= 14)",
    "precisionValue": 0.82,
    "recallValue": 0.34,
    "modelVersion": "v1"
  }
]
```

## Tổng kết API

| Bước | Method | Endpoint | Mục đích |
|------|--------|----------|----------|
| 1 | POST | `/api/campaigns/generate-test` | Tạo campaign |
| 2 | POST | `/api/ml/jobs` | Trigger uplift scoring (auto-import) |
| 3 | GET | `/api/ml/jobs/{jobId}` | Poll status (optional) |
| 4 | GET | `/api/targeting/campaigns/{id}/candidates` | Lấy candidates |
| 5 | POST | `/api/ml/jobs` | Trigger GP rules (auto-import) |
| 6 | GET | `/api/explainability/campaigns/{id}/gp-rules` | Lấy GP rules |

## Tổng kết Flow (Old vs New)

### Old Flow (Manual Upload)
```
POST /api/ml/jobs → COMPLETED → Frontend upload CSV → POST /api/targeting/scores/import
```

### New Flow (Auto Import)
```
POST /api/ml/jobs → COMPLETED → Backend auto-import → GET candidates
```

## Backend Event Flow

```
MlJobEventListener.handleUpliftScoringRequested()
  └─> job.markCompleted(resultPath)
  └─> eventPublisher.publishEvent(UpliftScoresImportRequestedEvent)

UpliftScoreImportListener.onUpliftScoresImportRequested()
  └─> upliftScoreCsvImporter.importFromFilePath(filePath, campaignId)
  └─> scores saved to targeting.customer_uplift_scores
```

## Database Tables

- `ml.ml_jobs` - job records (status, resultPath)
- `targeting.customer_uplift_scores` - uplift scores per customer (auto-imported)
- `targeting.customer_feature_snapshots` - feature snapshots
- `explainability.gp_rules` - GP rules

## Legacy Upload Endpoint (Deprecated)

```bash
# Vẫn còn nhưng không cần dùng nữa
POST /api/targeting/scores/import
Content-Type: multipart/form-data
file: <customer_uplift_scores.csv>
campaignId: abc-123
```

Frontend KHÔNG cần call endpoint này vì backend đã tự động import sau khi ML job COMPLETED.