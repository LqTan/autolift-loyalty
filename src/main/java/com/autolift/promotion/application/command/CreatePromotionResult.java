package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;

public record CreatePromotionResult(
    String id,
    String name,
    String description,
    PromotionType promotionType,
    BigDecimal value,
    BigDecimal minOrderAmount,
    String applicableCustomerSegment,
    String status,
    Instant startDate,
    Instant endDate) {}
