package com.autolift.explainability.domain.repository;

import com.autolift.explainability.domain.model.GpRule;
import java.util.List;

public interface GpRuleRepository {

  GpRule save(GpRule gpRule);

  List<GpRule> saveAll(List<GpRule> gpRules);

  List<GpRule> findByCampaignId(String campaignId);

  List<GpRule> findByCampaignIdOrderByF1ScoreDesc(String campaignId);
}