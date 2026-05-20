package com.autolift.campaign.api.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CreateBatchCampaignsRequest(List<CampaignBatchItem> campaigns) {

  public record CampaignBatchItem(
      String name,
      String description,
      Instant startDate,
      Instant endDate,
      BigDecimal budgetAmount,
      String budgetCurrency) {}
}