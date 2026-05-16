package com.autolift.campaign.api.query;

import java.math.BigDecimal;
import java.time.Instant;

public record CampaignResponse(
    String id,
    String name,
    String description,
    String status,
    Instant startDate,
    Instant endDate,
    BigDecimal budgetAmount,
    String budgetCurrency) {}
