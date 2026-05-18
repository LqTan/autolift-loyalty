package com.autolift.explainability.application.query;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetGpRulesHandler {

  private final GpRuleRepository repository;

  public GetGpRulesHandler(GpRuleRepository repository) {
    this.repository = repository;
  }

  public List<GpRuleView> handle(GetGpRulesQuery query) {
    var gpRules = repository.findByCampaignIdOrderByF1ScoreDesc(query.campaignId());
    return gpRules.stream()
        .map(this::toView)
        .toList();
  }

  private GpRuleView toView(GpRule gpRule) {
    return new GpRuleView(
        gpRule.getId().getId().toString(),
        gpRule.getCampaignId(),
        gpRule.getRuleText(),
        gpRule.getRuleExpression(),
        gpRule.getTargetLabel(),
        gpRule.getPrecisionValue(),
        gpRule.getRecallValue(),
        gpRule.getF1Score(),
        gpRule.getAccuracyValue(),
        gpRule.getCoverageValue(),
        gpRule.getModelVersion(),
        gpRule.getSourceFile(),
        gpRule.getCreatedAt()
    );
  }
}