# Uplift Modeling

## 1. Bài toán

Khác với dự đoán conversion thông thường ("Khách hàng có mua không?"), uplift model hỏi: "Ưu đãi có làm khách hàng thay đổi hành vi không?"

## 2. Công thức

```
uplift(x) = P(Y = 1 | X = x, T = 1) - P(Y = 1 | X = x, T = 0)
```

| Ký hiệu | Ý nghĩa |
|---------|---------|
| X | Đặc trưng khách hàng |
| T | Treatment (có nhận ưu đãi hay không) |
| Y | Conversion (có mua/sử dụng dịch vụ hay không) |

## 3. Ý nghĩa uplift_score

| Uplift Score | Segment | Hành động |
|--------------|---------|-----------|
| Cao (> 0.05) | Persuadable | Ưu tiên nhận ưu đãi |
| Gần 0 | Neutral | Không rõ ràng |
| Âm (< -0.01) | Do-not-target | Không nên target |

## 4. Dataset

**Criteo Uplift Prediction Dataset**
- features: f0, f1, ..., f11
- treatment: treatment indicator
- label: conversion hoặc visit

## 5. Mô hình T-Learner (Two-Model Approach)

```
Bước 1: Chia dữ liệu thành treatment group và control group

Bước 2: Train model_treatment trên nhóm T = 1
         P(Y = 1 | X, T = 1)

Bước 3: Train model_control trên nhóm T = 0
         P(Y = 1 | X, T = 0)

Bước 4: Với mỗi khách hàng x:
         uplift_score = model_treatment.predict_proba(x) - model_control.predict_proba(x)

Bước 5: Sắp xếp khách hàng theo uplift_score giảm dần

Bước 6: Chọn top K% khách hàng làm nhóm ưu tiên
```

## 6. Base Models

- Logistic Regression
- Random Forest
- XGBoost
- LightGBM

## 7. Metrics đánh giá

| Metric | Ý nghĩa |
|--------|---------|
| Uplift@K | Mức uplift khi target top K% |
| Qini Curve | Incremental gain khi target dần nhiều khách hàng |
| AUUC | Area Under Uplift Curve - so sánh tổng quát các mô hình |

## 8. Pipeline: Python ML -> Spring Boot

```
1. Load Criteo Uplift Prediction Dataset
2. Tách feature X, treatment T, label Y
3. Train model_treatment (T=1) và model_control (T=0)
4. Tính uplift_score = p_treatment - p_control
5. Đánh giá bằng Qini Curve, AUUC, Uplift@K
6. Export customer_uplift_scores.csv
7. Import CSV vào PostgreSQL targeting.customer_uplift_scores
8. Spring Boot targeting module query API
```

## 9. Cấu trúc ml/

```
ml/
├── notebooks/
│   └── uplift_criteo_experiment.ipynb
├── src/
│   ├── train_uplift_model.py
│   ├── evaluate_uplift_model.py
│   ├── batch_score_customers.py
│   └── export_scores.py
├── models/
│   └── uplift_t_learner_v1.joblib
└── outputs/
    └── customer_uplift_scores.csv
```

## 10. CSV Output Format

```csv
customer_id,campaign_id,uplift_score,treatment_probability,control_probability,segment,model_version,scored_at
```