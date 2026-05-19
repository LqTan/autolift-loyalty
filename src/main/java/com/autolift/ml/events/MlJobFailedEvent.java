package com.autolift.ml.events;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class MlJobFailedEvent extends ApplicationEvent {

  private final UUID jobId;
  private final MlJobType jobType;
  private final String campaignId;
  private final String errorMessage;
  private final Instant failedAt;

  public MlJobFailedEvent(
      UUID jobId, MlJobType jobType, String campaignId, String errorMessage, Instant failedAt) {
    super(jobId);
    this.jobId = jobId;
    this.jobType = jobType;
    this.campaignId = campaignId;
    this.errorMessage = errorMessage;
    this.failedAt = failedAt;
  }
}
