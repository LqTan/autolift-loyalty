package com.autolift.ml.domain.model;

import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MlJob {

  private final MlJobId id;
  private final MlJobType jobType;
  private final String campaignId;
  private final MlJobStatus status;
  private final String modelVersion;
  private final Map<String, Object> inputParams;
  private final String resultPath;
  private final String errorMessage;
  private final UUID upliftScoreJobId;
  private final Instant createdAt;
  private final Instant startedAt;
  private final Instant completedAt;

  protected MlJob() {
    this.id = null;
    this.jobType = null;
    this.campaignId = null;
    this.status = null;
    this.modelVersion = null;
    this.inputParams = null;
    this.resultPath = null;
    this.errorMessage = null;
    this.upliftScoreJobId = null;
    this.createdAt = null;
    this.startedAt = null;
    this.completedAt = null;
  }

  private MlJob(
      MlJobId id,
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

  public static MlJob createUpliftScoringJob(
      String campaignId, String modelVersion, Map<String, Object> inputParams) {
    return new MlJob(
        MlJobId.random(),
        MlJobType.UPLIFT_SCORING,
        campaignId,
        MlJobStatus.PENDING,
        modelVersion,
        inputParams,
        null,
        null,
        null,
        Instant.now(),
        null,
        null);
  }

  public static MlJob createGpRuleExtractionJob(
      String campaignId,
      String modelVersion,
      Map<String, Object> inputParams,
      UUID upliftScoreJobId) {
    return new MlJob(
        MlJobId.random(),
        MlJobType.GP_RULE_EXTRACTION,
        campaignId,
        MlJobStatus.PENDING,
        modelVersion,
        inputParams,
        null,
        null,
        upliftScoreJobId,
        Instant.now(),
        null,
        null);
  }

  public static MlJob createCustomerSeedJob() {
    return new MlJob(
        MlJobId.random(),
        MlJobType.CUSTOMER_SEED,
        null,
        MlJobStatus.PENDING,
        null,
        null,
        null,
        null,
        null,
        Instant.now(),
        null,
        null);
  }

  public static MlJob of(
      MlJobId id,
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
    return new MlJob(
        id,
        jobType,
        campaignId,
        status,
        modelVersion,
        inputParams,
        resultPath,
        errorMessage,
        upliftScoreJobId,
        createdAt,
        startedAt,
        completedAt);
  }

  public MlJob startWithProgress(int total) {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        MlJobStatus.RUNNING,
        this.modelVersion,
        this.inputParams,
        String.format("{\"imported\":0,\"failed\":0,\"total\":%d}", total),
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        Instant.now(),
        this.completedAt);
  }

  public MlJob markRunning() {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        MlJobStatus.RUNNING,
        this.modelVersion,
        this.inputParams,
        this.resultPath,
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        Instant.now(),
        this.completedAt);
  }

  public MlJob markCompleted(String resultPath) {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        MlJobStatus.COMPLETED,
        this.modelVersion,
        this.inputParams,
        resultPath,
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        this.startedAt,
        Instant.now());
  }

  public MlJob markFailed(String errorMessage) {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        MlJobStatus.FAILED,
        this.modelVersion,
        this.inputParams,
        this.resultPath,
        errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        this.startedAt,
        Instant.now());
  }

  public MlJob updateProgress(int imported, int failed) {
    int total = 0;
    if (this.resultPath != null && this.resultPath.contains("\"total\":")) {
      try {
        String totalStr = this.resultPath.split("\"total\":")[1].split("[,}]")[0];
        total = Integer.parseInt(totalStr.trim());
      } catch (Exception e) {
      }
    }
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        this.status,
        this.modelVersion,
        this.inputParams,
        String.format("{\"imported\":%d,\"failed\":%d,\"total\":%d}", imported, failed, total),
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        this.startedAt,
        this.completedAt);
  }
}
