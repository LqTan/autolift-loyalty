package com.autolift.loyalty.api.query;

import java.math.BigDecimal;
import java.time.Instant;

public record LoyaltyAccountResponse(
    String id,
    String customerId,
    BigDecimal pointsBalance,
    String tier,
    String status,
    Instant createdAt,
    Instant updatedAt) {}