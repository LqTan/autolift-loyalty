package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;

public record CreatePromotionCommand(
    String name,
    String description,
    PromotionType promotionType,
    BigDecimal value,
    BigDecimal minOrderAmount,
    String applicableCustomerSegment,
    Instant startDate,
    Instant endDate) {}
