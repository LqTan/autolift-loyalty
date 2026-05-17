package com.autolift.loyalty.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record LoyaltyAccountCreatedResult(
    String id,
    String customerId,
    BigDecimal pointsBalance,
    String tier,
    String status,
    Instant createdAt) {}