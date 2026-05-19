package com.autolift.ml.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class UpliftScoringRequestedEvent extends ApplicationEvent {

  private final UUID jobId;
  private final String campaignId;
  private final String modelVersion;
  private final Map<String, Object> inputParams;
  private final Instant requestedAt;

  public UpliftScoringRequestedEvent(
      UUID jobId,
      String campaignId,
      String modelVersion,
      Map<String, Object> inputParams,
      Instant requestedAt) {
    super(jobId);
    this.jobId = jobId;
    this.campaignId = campaignId;
    this.modelVersion = modelVersion;
    this.inputParams = inputParams;
    this.requestedAt = requestedAt;
  }
}
