package com.autolift.campaign.application.query;

import com.autolift.campaign.domain.model.Campaign;

import java.math.BigDecimal;
import java.time.Instant;

public record CampaignView(
    String id,
    String name,
    String description,
    String status,
    Instant startDate,
    Instant endDate,
    BigDecimal budgetAmount,
    String budgetCurrency
) {
    public static CampaignView fromDomain(Campaign campaign) {
        return new CampaignView(
            campaign.getId().getId().toString(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus().name(),
            campaign.getStartDate(),
            campaign.getEndDate(),
            campaign.getBudget().getAmount(),
            campaign.getBudget().getCurrency()
        );
    }
}