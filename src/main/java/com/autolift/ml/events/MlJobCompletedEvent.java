package com.autolift.ml.events;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class MlJobCompletedEvent extends ApplicationEvent {

  private final UUID jobId;
  private final MlJobType jobType;
  private final String campaignId;
  private final String resultPath;
  private final Instant completedAt;

  public MlJobCompletedEvent(
      UUID jobId, MlJobType jobType, String campaignId, String resultPath, Instant completedAt) {
    super(jobId);
    this.jobId = jobId;
    this.jobType = jobType;
    this.campaignId = campaignId;
    this.resultPath = resultPath;
    this.completedAt = completedAt;
  }
}