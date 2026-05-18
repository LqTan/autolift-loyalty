package com.autolift.targeting.domain.model;

import com.autolift.targeting.domain.valueobject.TargetingSegment;
import com.autolift.targeting.domain.valueobject.UpliftScoreId;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class CustomerUpliftScore {

  private final UpliftScoreId id;
  private final String customerId;
  private final String campaignId;
  private final BigDecimal upliftScore;
  private final BigDecimal treatmentProbability;
  private final BigDecimal controlProbability;
  private final TargetingSegment segment;
  private final String modelVersion;
  private final Instant scoredAt;

  protected CustomerUpliftScore() {
    this.id = null;
    this.customerId = null;
    this.campaignId = null;
    this.upliftScore = null;
    this.treatmentProbability = null;
    this.controlProbability = null;
    this.segment = null;
    this.modelVersion = null;
    this.scoredAt = null;
  }

  private CustomerUpliftScore(
      UpliftScoreId id,
      String customerId,
      String campaignId,
      BigDecimal upliftScore,
      BigDecimal treatmentProbability,
      BigDecimal controlProbability,
      TargetingSegment segment,
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

  public static CustomerUpliftScore create(
      String customerId,
      String campaignId,
      BigDecimal upliftScore,
      BigDecimal treatmentProbability,
      BigDecimal controlProbability,
      String modelVersion) {
    return new CustomerUpliftScore(
        UpliftScoreId.random(),
        customerId,
        campaignId,
        upliftScore,
        treatmentProbability,
        controlProbability,
        TargetingSegment.from(upliftScore.doubleValue()),
        modelVersion,
        Instant.now());
  }

  public static CustomerUpliftScore of(
      UpliftScoreId id,
      String customerId,
      String campaignId,
      BigDecimal upliftScore,
      BigDecimal treatmentProbability,
      BigDecimal controlProbability,
      TargetingSegment segment,
      String modelVersion,
      Instant scoredAt) {
    return new CustomerUpliftScore(
        id,
        customerId,
        campaignId,
        upliftScore,
        treatmentProbability,
        controlProbability,
        segment,
        modelVersion,
        scoredAt);
  }
}