package com.autolift.loyalty.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record LoyaltyAccountView(
    String id,
    String customerId,
    BigDecimal pointsBalance,
    String tier,
    String status,
    Instant createdAt,
    Instant updatedAt) {}
