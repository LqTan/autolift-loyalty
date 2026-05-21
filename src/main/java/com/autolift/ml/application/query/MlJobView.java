package com.autolift.ml.application.query;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MlJobView {
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
  private Integer progress;
  private String message;

  public static MlJobView from(MlJob job) {
    return new MlJobView(
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
        job.getCompletedAt(),
        job.getProgress(),
        job.getMessage());
  }

  public MlJobView(
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
      Instant completedAt,
      Integer progress,
      String message) {
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
    this.progress = progress;
    this.message = message;
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

  public Integer getProgress() {
    return progress;
  }

  public String getMessage() {
    return message;
  }
}
