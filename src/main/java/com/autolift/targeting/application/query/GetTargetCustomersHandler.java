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
    return repository
        .findTopByCampaignIdOrderByUpliftScoreDesc(query.campaignId(), query.limit())
        .stream()
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
