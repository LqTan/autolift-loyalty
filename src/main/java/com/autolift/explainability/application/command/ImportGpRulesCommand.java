package com.autolift.explainability.application.command;

import java.util.List;

public record ImportGpRulesCommand(List<GpRuleImportItem> items) {
  public record GpRuleImportItem(
      String campaignId,
      String ruleText,
      String ruleExpression,
      String targetLabel,
      java.math.BigDecimal precisionValue,
      java.math.BigDecimal recallValue,
      java.math.BigDecimal f1Score,
      java.math.BigDecimal accuracyValue,
      java.math.BigDecimal coverageValue,
      String modelVersion,
      String sourceFile) {}
}
