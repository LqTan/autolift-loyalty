package com.autolift.explainability.api.query;

import com.autolift.explainability.application.query.GetGpRulesHandler;
import com.autolift.explainability.application.query.GetGpRulesQuery;
import com.autolift.explainability.application.query.GpRuleView;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/explainability")
public class ExplainabilityQueryController {

  private final GetGpRulesHandler getGpRulesHandler;

  public ExplainabilityQueryController(GetGpRulesHandler getGpRulesHandler) {
    this.getGpRulesHandler = getGpRulesHandler;
  }

  @GetMapping("/campaigns/{campaignId}/gp-rules")
  public ResponseEntity<List<GpRuleView>> getGpRulesByCampaign(@PathVariable String campaignId) {
    var query = new GetGpRulesQuery(campaignId);
    var rules = getGpRulesHandler.handle(query);
    return ResponseEntity.ok(rules);
  }
}
