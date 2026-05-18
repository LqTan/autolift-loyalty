package com.autolift.targeting.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "customer_uplift_scores", schema = "targeting")
public class CustomerUpliftScoreJpaEntity {

  @Id private UUID id;

  @Column(name = "customer_id") private String customerId;

  @Column(name = "campaign_id") private String campaignId;

  @Column(name = "uplift_score", precision = 10, scale = 6) private BigDecimal upliftScore;

  @Column(name = "treatment_probability", precision = 10, scale = 6) private BigDecimal treatmentProbability;

  @Column(name = "control_probability", precision = 10, scale = 6) private BigDecimal controlProbability;

  @Column private String segment;

  @Column(name = "model_version") private String modelVersion;

  @Column(name = "scored_at") private Instant scoredAt;

  public CustomerUpliftScoreJpaEntity(
      UUID id,
      String customerId,
      String campaignId,
      BigDecimal upliftScore,
      BigDecimal treatmentProbability,
      BigDecimal controlProbability,
      String segment,
      String modelVersion,
      Instant scoredAt) {
    this.id = id;
    this.customerId = customerId;
    this.campaignId = campaignId;
    this.upliftScore = upliftScore;
    this.treatmentProbability = treatmentProbability;
    this.controlProbability = controlProbability;
    this.segment = segment;
    this.modelVersion = modelVersion;
    this.scoredAt = scoredAt;
  }
}