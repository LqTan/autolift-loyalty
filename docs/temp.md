# Customer Seed Flow - FE Integration

## Bước 1: Bắt đầu seed

```
POST /api/customers/seed
```

**Response 202:**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PENDING"
}
```

## Bước 2: Poll progress (mỗi 2-5 giây)

```
GET /api/ml/jobs/{jobId}
```

**Response 200:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "jobType": "CUSTOMER_SEED",
  "status": "RUNNING",
  "resultPath": "{\"imported\":50000,\"failed\":0}"
}
```

**Các status:**
| Status | Ý nghĩa |
|--------|---------|
| `PENDING` | Job mới tạo |
| `RUNNING` | Đang import |
| `COMPLETED` | Import xong |
| `FAILED` | Import lỗi |

**Tính % progress:**
```javascript
const data = JSON.parse(resultPath);
const percent = Math.round(data.imported / 400162 * 100);
```

**Loop polling:**
```javascript
while (status === 'PENDING' || status === 'RUNNING') {
  await sleep(3000);
  const job = await fetch(`/api/ml/jobs/${jobId}`);
  status = job.status;
  // Update UI với job.resultPath
}
```

## Bước 3: Lấy danh sách customers (sau khi COMPLETED)

```
GET /api/customers?page=0&size=20&sortBy=createdAt&sortDir=DESC
```

**Response 200:**
```json
{
  "content": [
    {
      "id": "557b5575-7920-5bfe-b338-986baf22a9ba",
      "name": "X5 Customer 00001276",
      "email": "000012768d@x5.client",
      "phone": "",
      "segment": "NORMAL",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 400162,
  "totalPages": 20009,
  "size": 20,
  "number": 0
}
```

## Tổng kết API

| Bước | Method | Endpoint | Mục đích |
|------|--------|----------|----------|
| 1 | POST | `/api/customers/seed` | Bắt đầu seed |
| 2 | GET | `/api/ml/jobs/{jobId}` | Poll progress |
| 3 | GET | `/api/customers` | Lấy danh sách |