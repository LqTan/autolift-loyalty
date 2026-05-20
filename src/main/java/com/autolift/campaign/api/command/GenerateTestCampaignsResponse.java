package com.autolift.campaign.api.command;

import java.math.BigDecimal;
import java.util.List;

public record GenerateTestCampaignsResponse(int generated, List<TestCampaignResult> campaigns) {

  public record TestCampaignResult(String id, String name, String status) {}
}