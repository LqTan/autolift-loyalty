# Uplift Modeling

## 1. Bài toán

Khác với dự đoán conversion thông thường ("Khách hàng có mua không?"), uplift model hỏi: "Ưu đãi có làm khách hàng thay đổi hành vi không?"

## 2. Công thức

```
uplift(x) = P(Y = 1 | X = x, T = 1) - P(Y = 1 | X = x, T = 0)
```

| Ký hiệu | Ý nghĩa |
|---------|---------|
| X | Đặc trưng khách hàng, được xây dựng từ thông tin khách hàng và lịch sử mua hàng |
| T | Treatment, tức là có nhận communication/ưu đãi hay không |
| Y | Outcome/target, tức là có mua hàng sau chiến dịch hay không |

## 3. Ý nghĩa uplift_score

| Uplift Score | Segment | Hành động |
|--------------|---------|-----------|
| Cao (> 0.05) | Persuadable | Ưu tiên nhận ưu đãi |
| Gần 0 | Neutral | Không rõ ràng |
| Âm (< -0.01) | Do-not-target | Không nên target |

## 4. Dataset

**X5 RetailHero Uplift Modeling Dataset**

Dữ liệu chính:
- `uplift_train.csv` - tập train có customer_id, treatment_flg và target
- `uplift_test.csv` - tập khách hàng cần dự đoán uplift
- `clients.csv` - thông tin khách hàng
- `products.csv` - thông tin sản phẩm
- `purchases.csv` - lịch sử mua hàng trước communication/campaign

Feature nghiệp vụ xây dựng từ `purchases.csv`:
- `recency_days` - số ngày từ lần mua gần nhất đến thời điểm chiến dịch
- `frequency_90d` - số lần mua trong 90 ngày trước chiến dịch
- `monetary_90d` - tổng giá trị mua hàng trong 90 ngày
- `avg_basket_value` - giá trị trung bình mỗi giao dịch
- `total_quantity_90d` - tổng số lượng sản phẩm đã mua
- `unique_product_count` - số sản phẩm khác nhau đã mua
- `unique_category_count` - số nhóm hàng khác nhau đã mua
- `favorite_category` - nhóm hàng khách mua nhiều nhất

## 5. Mô hình T-Learner (Two-Model Approach)

```
Bước 1: Load dữ liệu X5 RetailHero
Bước 2: Tạo feature nghiệp vụ từ purchases.csv
Bước 3: Join feature với uplift_train theo customer_id
Bước 4: Tách dữ liệu X, treatment T = treatment_flg, label Y = target
Bước 5: Train model_treatment trên nhóm T = 1
         P(Y = 1 | X, T = 1)
Bước 6: Train model_control trên nhóm T = 0
         P(Y = 1 | X, T = 0)
Bước 7: Với mỗi khách hàng x:
         uplift_score = model_treatment.predict_proba(x) - model_control.predict_proba(x)
Bước 8: Sắp xếp khách hàng theo uplift_score giảm dần
Bước 9: Chọn top K% khách hàng làm nhóm ưu tiên
```

## 6. Base Models

- Logistic Regression
- Random Forest
- XGBoost
- LightGBM
- CatBoost (nếu cần xử lý categorical feature tốt hơn)

## 7. Metrics đánh giá

| Metric | Ý nghĩa |
|--------|---------|
| Uplift@K | Mức uplift khi target top K% khách hàng theo uplift_score |
| Qini Curve | Đường cong thể hiện incremental gain khi target dần nhiều khách hàng |
| AUUC | Area Under Uplift Curve - dùng để so sánh tổng quát giữa các mô hình |
| Qini AUC | Diện tích dưới Qini curve |
| Average Added Conversion | Mức tăng chuyển đổi trung bình khi chỉ chọn nhóm khách hàng được xếp hạng cao nhất |

## 8. Pipeline: Python ML -> Spring Boot

```
1. Load X5 RetailHero Dataset
2. Join clients, products và purchases
3. Tạo feature hành vi mua hàng theo customer_id
4. Join feature với uplift_train
5. Tách X, treatment T = treatment_flg, label Y = target
6. Train model_treatment (T=1) và model_control (T=0)
7. Tính uplift_score = p_treatment - p_control
8. Đánh giá bằng Qini Curve, AUUC, Uplift@K
9. Export customer_uplift_scores.csv
10. Import CSV vào PostgreSQL targeting.customer_uplift_scores
11. Spring Boot targeting module query API
```

## 9. Cấu trúc ml/

```
ml/
├── notebooks/
│   ├── autolift_x5_materials_export_lite_chunked.ipynb
│   ├── uplift_x5_retailhero_experiment.ipynb
│   ├── gp_rule_extraction_experiment.ipynb
│   └── gp_xor_demo.ipynb
├── src/
│   ├── build_x5_features.py
│   ├── train_uplift_model.py
│   ├── evaluate_uplift_model.py
│   ├── batch_score_customers.py
│   ├── export_scores.py
│   ├── build_gp_input.py
│   ├── train_gp_rules.py
│   └── gp_xor_demo.py
├── models/
│   ├── uplift_t_learner_x5_v1.joblib
│   └── gp_rule_model_v1.joblib
└── outputs/
    ├── customer_uplift_scores.csv
    ├── gp_rules.csv
    ├── economic_summary.csv
    └── xor_gp_demo_results.csv
```

## 10. CSV Output Format

```csv
customer_id,campaign_id,uplift_score,treatment_probability,control_probability,segment,model_version,scored_at
```

## 11. Genetic Programming (GP) - Explainability

GP sinh luật logic diễn giải nhóm khách hàng được ưu tiên.

**Nguyên tắc:** Không dùng trực tiếp uplift_score làm terminal condition.

**Dữ liệu đầu vào cho GP** (`gp_input.csv`):
```
customer_id, campaign_id, recency_days, frequency_90d, monetary_90d, avg_basket_value,
unique_product_count, unique_category_count, favorite_category, uplift_score, target_flag
```

**Nguyên tắc GP:**
- target_flag = 1: khách hàng thuộc nhóm top K% uplift_score
- target_flag = 0: khách hàng còn lại
- GP dùng các feature nghiệp vụ để mô tả nhóm target_flag = 1

**Ví dụ luật GP:**
```
(frequency_90d > 5) AND (recency_days <= 14) AND (avg_basket_value > 300000)
```

**Cấu trúc output GP** (`gp_rules.csv`):
```
id, campaign_id, rule_text, rule_expression, target_label, precision_value,
recall_value, f1_score, accuracy_value, coverage_value, model_version, source_file, created_at
```

## 12. GP XOR Demo

XOR dùng để kiểm chứng Genetic Programming ở mức logic cơ bản.

**Bảng chân trị XOR:**
```
A | B | XOR
0 | 0 | 0
0 | 1 | 1
1 | 0 | 1
1 | 1 | 0
```

**Function set:** AND, OR, NOT

**Terminal set:** A, B, True, False

**Fitness:** fitness(rule) = số dòng XOR được dự đoán đúng / 4

**Output:** `xor_gp_demo_results.csv`
```
run_id, best_rule_text, best_fitness, generation_count, population_size, mutation_rate, crossover_rate
```

## 13. Tại sao X5 thay vì Criteo?

- Criteo có conversion rất thấp và feature ẩn danh, khó giải thích nghiệp vụ
- Starbucks bản uplift sạch nhưng feature V1-V7 cũng ẩn danh
- X5 RetailHero có treatment/control, outcome và purchase history
- X5 cho phép tạo feature nghiệp vụ dễ hiểu, phù hợp hơn với GP rule explainability
- X5 gần với bài toán retail/promotion, khớp hơn với Autolift Loyalty