package com.autolift.targeting.domain.repository;

import com.autolift.targeting.domain.model.CustomerFeatureSnapshot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerFeatureSnapshotRepository {

  CustomerFeatureSnapshot save(CustomerFeatureSnapshot snapshot);

  void saveAll(List<CustomerFeatureSnapshot> snapshots);

  Optional<CustomerFeatureSnapshot> findById(UUID id);

  List<CustomerFeatureSnapshot> findByCustomerId(String customerId);

  Optional<CustomerFeatureSnapshot> findByCustomerIdAndCampaignId(String customerId, String campaignId);
}