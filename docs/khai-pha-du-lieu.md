# Báo cáo môn Khai phá dữ liệu

## Tên đề tài
**Ứng dụng Uplift Modeling trong lựa chọn khách hàng nhận ưu đãi**

## Mô tả bài toán

Trong hệ thống khuyến mãi, việc gửi ưu đãi cho toàn bộ khách hàng có thể gây lãng phí ngân sách, vì có những khách hàng vốn đã mua hàng dù không nhận ưu đãi. Do đó, đề tài bổ sung mô-đun Uplift Modeling nhằm xác định nhóm khách hàng có khả năng thay đổi hành vi khi nhận treatment. Nhóm khách hàng này được gọi là **Persuadable**.

## Phương pháp

Bài toán được mô hình hóa bằng dữ liệu gồm đặc trưng khách hàng X, biến treatment T và nhãn conversion Y. Uplift score được tính bằng chênh lệch giữa xác suất chuyển đổi khi nhận ưu đãi và xác suất chuyển đổi khi không nhận ưu đãi:

```
uplift(x) = P(Y = 1 | X = x, T = 1) - P(Y = 1 | X = x, T = 0)
```

## Mô hình Two-Model Approach (T-Learner)

Đề tài sử dụng Two-Model Approach. Một mô hình được huấn luyện trên nhóm treatment để ước lượng xác suất chuyển đổi khi nhận ưu đãi, trong khi mô hình còn lại được huấn luyện trên nhóm control để ước lượng xác suất chuyển đổi khi không nhận ưu đãi. Chênh lệch giữa hai xác suất này được dùng làm uplift score. Các khách hàng có uplift score cao được xếp vào nhóm Persuadable và được ưu tiên nhận campaign hoặc voucher.

## Dataset

Thực nghiệm sử dụng **Criteo Uplift Prediction Dataset**, với các đặc trưng ẩn danh, treatment indicator và nhãn visit/conversion.

## Metrics

Mô hình được đánh giá bằng các chỉ số chuyên biệt cho uplift modeling:
- **Uplift@K** - mức uplift đạt được khi target top K%
- **Qini Curve** - đường cong incremental gain
- **AUUC** - Area Under Uplift Curve

## Kết quả đầu ra

Kết quả đầu ra của mô hình là bảng **customer uplift score**, được tích hợp vào module targeting của hệ thống backend.

## Baseline so sánh

| Model | Mô tả |
|-------|-------|
| Random targeting | Chọn ngẫu nhiên |
| Response model | Dự đoán xác suất mua |
| T-Learner (Main) | RandomForest hoặc LightGBM |