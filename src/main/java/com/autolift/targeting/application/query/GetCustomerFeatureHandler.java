package com.autolift.targeting.application.query;

import com.autolift.targeting.domain.model.CustomerFeatureSnapshot;
import com.autolift.targeting.domain.repository.CustomerFeatureSnapshotRepository;
import org.springframework.stereotype.Component;

@Component
public class GetCustomerFeatureHandler {

  private final CustomerFeatureSnapshotRepository repository;

  public GetCustomerFeatureHandler(CustomerFeatureSnapshotRepository repository) {
    this.repository = repository;
  }

  public CustomerFeatureView handle(GetCustomerFeatureQuery query) {
    return repository
        .findByCustomerIdAndCampaignId(query.customerId(), query.campaignId())
        .map(this::toView)
        .orElse(null);
  }

  private CustomerFeatureView toView(CustomerFeatureSnapshot snapshot) {
    return new CustomerFeatureView(
        snapshot.getCustomerId(),
        snapshot.getRecencyDays(),
        snapshot.getFrequency90d(),
        snapshot.getMonetary90d(),
        snapshot.getAvgBasketValue(),
        snapshot.getTotalQuantity90d(),
        snapshot.getUniqueProductCount(),
        snapshot.getUniqueCategoryCount(),
        snapshot.getFavoriteCategory(),
        snapshot.getFeatureVersion(),
        snapshot.getCreatedAt());
  }
}
