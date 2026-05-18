package com.autolift.explainability.domain.model;

import com.autolift.explainability.domain.valueobject.GpRuleId;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class GpRule {

  private final GpRuleId id;
  private final String campaignId;
  private final String ruleText;
  private final String ruleExpression;
  private final String targetLabel;
  private final BigDecimal precisionValue;
  private final BigDecimal recallValue;
  private final BigDecimal f1Score;
  private final BigDecimal accuracyValue;
  private final BigDecimal coverageValue;
  private final String modelVersion;
  private final String sourceFile;
  private final Instant createdAt;

  protected GpRule() {
    this.id = null;
    this.campaignId = null;
    this.ruleText = null;
    this.ruleExpression = null;
    this.targetLabel = null;
    this.precisionValue = null;
    this.recallValue = null;
    this.f1Score = null;
    this.accuracyValue = null;
    this.coverageValue = null;
    this.modelVersion = null;
    this.sourceFile = null;
    this.createdAt = null;
  }

  private GpRule(
      GpRuleId id,
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

  public static GpRule create(
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
      String sourceFile) {
    return new GpRule(
        GpRuleId.random(),
        campaignId,
        ruleText,
        ruleExpression,
        targetLabel,
        precisionValue,
        recallValue,
        f1Score,
        accuracyValue,
        coverageValue,
        modelVersion,
        sourceFile,
        Instant.now());
  }

  public static GpRule of(
      GpRuleId id,
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
    return new GpRule(
        id,
        campaignId,
        ruleText,
        ruleExpression,
        targetLabel,
        precisionValue,
        recallValue,
        f1Score,
        accuracyValue,
        coverageValue,
        modelVersion,
        sourceFile,
        createdAt);
  }
}