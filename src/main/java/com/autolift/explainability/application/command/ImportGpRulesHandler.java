package com.autolift.explainability.application.command;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import org.springframework.stereotype.Component;

@Component
public class ImportGpRulesHandler {

  private final GpRuleRepository repository;

  public ImportGpRulesHandler(GpRuleRepository repository) {
    this.repository = repository;
  }

  public int handle(ImportGpRulesCommand command) {
    var items = command.items();
    var gpRules =
        items.stream()
            .map(
                item ->
                    GpRule.create(
                        item.campaignId(),
                        item.ruleText(),
                        item.ruleExpression(),
                        item.targetLabel(),
                        item.precisionValue(),
                        item.recallValue(),
                        item.f1Score(),
                        item.accuracyValue(),
                        item.coverageValue(),
                        item.modelVersion(),
                        item.sourceFile()))
            .toList();
    repository.saveAll(gpRules);
    return gpRules.size();
  }
}
