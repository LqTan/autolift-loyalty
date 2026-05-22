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
  private final Integer progress;
  private final String message;
  private final Map<String, Object> metrics;

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
    this.progress = null;
    this.message = null;
    this.metrics = null;
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
      Instant completedAt,
      Integer progress,
      String message,
      Map<String, Object> metrics) {
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
    this.metrics = metrics;
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
        null,
        0,
        "Queued",
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
        null,
        0,
        "Queued",
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
        null,
        0,
        "Queued",
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
      Instant completedAt,
      Integer progress,
      String message,
      Map<String, Object> metrics) {
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
        completedAt,
        progress,
        message,
        metrics);
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
        this.completedAt,
        0,
        "Starting...",
        this.metrics);
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
        this.completedAt,
        0,
        "Running",
        this.metrics);
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
        Instant.now(),
        100,
        "Completed",
        this.metrics);
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
        Instant.now(),
        this.progress,
        "Failed",
        this.metrics);
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
    int pct = total > 0 ? (imported + failed) * 100 / total : 0;
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
        this.completedAt,
        pct,
        String.format("Imported %d/%d", imported, total),
        this.metrics);
  }

  public MlJob withProgress(int progress, String message) {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        this.status,
        this.modelVersion,
        this.inputParams,
        this.resultPath,
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        this.startedAt,
        this.completedAt,
        progress,
        message,
        this.metrics);
  }

  public MlJob withMetrics(Map<String, Object> metrics) {
    return new MlJob(
        this.id,
        this.jobType,
        this.campaignId,
        this.status,
        this.modelVersion,
        this.inputParams,
        this.resultPath,
        this.errorMessage,
        this.upliftScoreJobId,
        this.createdAt,
        this.startedAt,
        this.completedAt,
        this.progress,
        this.message,
        metrics);
  }
}
