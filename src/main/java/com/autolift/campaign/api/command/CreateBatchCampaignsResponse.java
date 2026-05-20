package com.autolift.campaign.api.command;

import java.util.List;

public record CreateBatchCampaignsResponse(List<BatchCampaignResult> created) {

  public record BatchCampaignResult(
      String id,
      String name,
      String status) {}
}