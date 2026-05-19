package com.autolift.explainability.infrastructure.persistence.entity;

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
@Table(name = "gp_rules", schema = "explainability")
public class GpRuleJpaEntity {

  @Id private UUID id;

  @Column(name = "campaign_id")
  private String campaignId;

  @Column(name = "rule_text", columnDefinition = "TEXT")
  private String ruleText;

  @Column(name = "rule_expression", columnDefinition = "TEXT")
  private String ruleExpression;

  @Column(name = "target_label")
  private String targetLabel;

  @Column(name = "precision_value", precision = 10, scale = 6)
  private BigDecimal precisionValue;

  @Column(name = "recall_value", precision = 10, scale = 6)
  private BigDecimal recallValue;

  @Column(name = "f1_score", precision = 10, scale = 6)
  private BigDecimal f1Score;

  @Column(name = "accuracy_value", precision = 10, scale = 6)
  private BigDecimal accuracyValue;

  @Column(name = "coverage_value", precision = 10, scale = 6)
  private BigDecimal coverageValue;

  @Column(name = "model_version")
  private String modelVersion;

  @Column(name = "source_file")
  private String sourceFile;

  @Column(name = "created_at")
  private Instant createdAt;

  public GpRuleJpaEntity(
      UUID id,
      String campaignId,
      String ruleText,
      String ruleExpression,
      String targetLabel,
      BigDecimal precisionValue,
      BigDecimal recallValue,
      BigDecimal f1Score,
      BigDecimal accuracyValue,
      BigDecimal coverageValue,
      String modelVersion,
      String sourceFile,
      Instant createdAt) {
    this.id = id;
    this.campaignId = campaignId;
    this.ruleText = ruleText;
    this.ruleExpression = ruleExpression;
    this.targetLabel = targetLabel;
    this.precisionValue = precisionValue;
    this.recallValue = recallValue;
    this.f1Score = f1Score;
    this.accuracyValue = accuracyValue;
    this.coverageValue = coverageValue;
    this.modelVersion = modelVersion;
    this.sourceFile = sourceFile;
    this.createdAt = createdAt;
  }
}
