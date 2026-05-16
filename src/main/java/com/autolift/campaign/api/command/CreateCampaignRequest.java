package com.autolift.campaign.api.command;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCampaignRequest(
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    BigDecimal budgetAmount,
    String budgetCurrency) {}
