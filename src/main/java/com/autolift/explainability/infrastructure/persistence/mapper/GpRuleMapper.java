package com.autolift.explainability.infrastructure.persistence.mapper;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.valueobject.GpRuleId;
import com.autolift.explainability.infrastructure.persistence.entity.GpRuleJpaEntity;

public class GpRuleMapper {

  public static GpRule toDomain(GpRuleJpaEntity entity) {
    return GpRule.of(
        GpRuleId.of(entity.getId(), entity.getCreatedAt()),
        entity.getCampaignId(),
        entity.getRuleText(),
        entity.getRuleExpression(),
        entity.getTargetLabel(),
        entity.getPrecisionValue(),
        entity.getRecallValue(),
        entity.getF1Score(),
        entity.getAccuracyValue(),
        entity.getCoverageValue(),
        entity.getModelVersion(),
        entity.getSourceFile(),
        entity.getCreatedAt());
  }

  public static GpRuleJpaEntity toEntity(GpRule domain) {
    return new GpRuleJpaEntity(
        domain.getId().getId(),
        domain.getCampaignId(),
        domain.getRuleText(),
        domain.getRuleExpression(),
        domain.getTargetLabel(),
        domain.getPrecisionValue(),
        domain.getRecallValue(),
        domain.getF1Score(),
        domain.getAccuracyValue(),
        domain.getCoverageValue(),
        domain.getModelVersion(),
        domain.getSourceFile(),
        domain.getCreatedAt());
  }
}
