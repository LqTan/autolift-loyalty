package com.autolift.ml.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class GpRulesExtractionRequestedEvent extends ApplicationEvent {

  private final UUID jobId;
  private final String campaignId;
  private final UUID upliftScoreJobId;
  private final String modelVersion;
  private final Map<String, Object> inputParams;
  private final Instant requestedAt;

  public GpRulesExtractionRequestedEvent(
      UUID jobId,
      String campaignId,
      UUID upliftScoreJobId,
      String modelVersion,
      Map<String, Object> inputParams,
      Instant requestedAt) {
    super(jobId);
    this.jobId = jobId;
    this.campaignId = campaignId;
    this.upliftScoreJobId = upliftScoreJobId;
    this.modelVersion = modelVersion;
    this.inputParams = inputParams;
    this.requestedAt = requestedAt;
  }

  public UUID getJobId() {
    return jobId;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public UUID getUpliftScoreJobId() {
    return upliftScoreJobId;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public Map<String, Object> getInputParams() {
    return inputParams;
  }

  public Instant getRequestedAt() {
    return requestedAt;
  }
}
