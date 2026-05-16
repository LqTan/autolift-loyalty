package com.autolift.promotion.application.query;

import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Builder
public record PromotionView(
    String id,
    String name,
    String description,
    PromotionType promotionType,
    BigDecimal value,
    BigDecimal minOrderAmount,
    String applicableCustomerSegment,
    String status,
    Instant startDate,
    Instant endDate,
    Instant createdAt,
    Instant updatedAt) {}
