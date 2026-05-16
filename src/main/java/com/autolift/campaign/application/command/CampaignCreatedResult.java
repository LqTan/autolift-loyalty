package com.autolift.campaign.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record CampaignCreatedResult(
    String id,
    String name,
    String description,
    String status,
    Instant startDate,
    Instant endDate,
    BigDecimal budgetAmount,
    String budgetCurrency
) {}