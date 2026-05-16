package com.autolift.promotion.api.command;

import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;

public record CreatePromotionRequest(
    String name,
    String description,
    PromotionType promotionType,
    BigDecimal value,
    BigDecimal minOrderAmount,
    String applicableCustomerSegment,
    Instant startDate,
    Instant endDate) {}
