package com.autolift.ml.application.query;

import java.util.UUID;

public class GetMlJobQuery {
  private UUID jobId;
  private String campaignId;

  public GetMlJobQuery() {}

  public GetMlJobQuery(UUID jobId) {
    this.jobId = jobId;
  }

  public UUID getJobId() { return jobId; }
  public void setJobId(UUID jobId) { this.jobId = jobId; }
  public String getCampaignId() { return campaignId; }
  public void setCampaignId(String campaignId) { this.campaignId = campaignId; }
}