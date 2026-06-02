package com.autolift.targeting.application.query;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.repository.CustomerUpliftScoreRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetTargetCustomersHandler {

  private final CustomerUpliftScoreRepository repository;

  public GetTargetCustomersHandler(CustomerUpliftScoreRepository repository) {
    this.repository = repository;
  }

  public List<TargetCustomerView> handle(GetTargetCustomersQuery query) {
    return repository.findByCampaignId(query.campaignId()).stream()
        .sorted((a, b) -> b.getUpliftScore().compareTo(a.getUpliftScore()))
        .limit(query.limit())
        .map(this::toView)
        .toList();
  }

  private TargetCustomerView toView(CustomerUpliftScore score) {
    return new TargetCustomerView(
        score.getCustomerId(),
        score.getUpliftScore(),
        score.getSegment(),
        score.getTreatmentProbability(),
        score.getControlProbability());
  }
}
