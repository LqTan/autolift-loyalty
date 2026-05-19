package com.autolift.ml.api.command;

import com.autolift.ml.application.query.MlJobView;
import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MlJobResponse {
  private UUID id;
  private MlJobType jobType;
  private String campaignId;
  private MlJobStatus status;
  private String modelVersion;
  private Map<String, Object> inputParams;
  private String resultPath;
  private String errorMessage;
  private UUID upliftScoreJobId;
  private Instant createdAt;
  private Instant startedAt;
  private Instant completedAt;

  public static MlJobResponse from(MlJob job) {
    return new MlJobResponse(
        job.getId().getId(),
        job.getJobType(),
        job.getCampaignId(),
        job.getStatus(),
        job.getModelVersion(),
        job.getInputParams(),
        job.getResultPath(),
        job.getErrorMessage(),
        job.getUpliftScoreJobId(),
        job.getCreatedAt(),
        job.getStartedAt(),
        job.getCompletedAt());
  }

  public static MlJobResponse from(MlJobView view) {
    return new MlJobResponse(
        view.getId(),
        view.getJobType(),
        view.getCampaignId(),
        view.getStatus(),
        view.getModelVersion(),
        view.getInputParams(),
        view.getResultPath(),
        view.getErrorMessage(),
        view.getUpliftScoreJobId(),
        view.getCreatedAt(),
        view.getStartedAt(),
        view.getCompletedAt());
  }

  public MlJobResponse(
      UUID id,
      MlJobType jobType,
      String campaignId,
      MlJobStatus status,
      String modelVersion,
      Map<String, Object> inputParams,
      String resultPath,
      String errorMessage,
      UUID upliftScoreJobId,
      Instant createdAt,
      Instant startedAt,
      Instant completedAt) {
    this.id = id;
    this.jobType = jobType;
    this.campaignId = campaignId;
    this.status = status;
    this.modelVersion = modelVersion;
    this.inputParams = inputParams;
    this.resultPath = resultPath;
    this.errorMessage = errorMessage;
    this.upliftScoreJobId = upliftScoreJobId;
    this.createdAt = createdAt;
    this.startedAt = startedAt;
    this.completedAt = completedAt;
  }

  public UUID getId() {
    return id;
  }

  public MlJobType getJobType() {
    return jobType;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public MlJobStatus getStatus() {
    return status;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public Map<String, Object> getInputParams() {
    return inputParams;
  }

  public String getResultPath() {
    return resultPath;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public UUID getUpliftScoreJobId() {
    return upliftScoreJobId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }
}
