package com.autolift.explainability.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record GpRuleView(
    String id,
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
    Instant createdAt) {}
