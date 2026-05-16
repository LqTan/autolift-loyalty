package com.autolift.campaign.application.command;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateCampaignCommand(
    String name,
    String description,
    Instant startDate,
    Instant endDate,
    BigDecimal budgetAmount,
    String budgetCurrency) {}
