package com.autolift.ml.infrastructure.persistence.entity;

import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "ml_jobs", schema = "ml")
public class MlJobJpaEntity {

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "job_type", nullable = false)
  private MlJobType jobType;

  @Column(name = "campaign_id")
  private String campaignId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MlJobStatus status;

  @Column(name = "model_version")
  private String modelVersion;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "input_params", columnDefinition = "jsonb")
  private String inputParams;

  @Column(name = "result_path")
  private String resultPath;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "uplift_score_job_id")
  private UUID upliftScoreJobId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "started_at")
  private Instant startedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  public MlJobJpaEntity(
      UUID id,
      MlJobType jobType,
      String campaignId,
      MlJobStatus status,
      String modelVersion,
      String inputParams,
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
}