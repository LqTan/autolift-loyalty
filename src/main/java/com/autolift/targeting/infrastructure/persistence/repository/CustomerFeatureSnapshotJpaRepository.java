package com.autolift.targeting.infrastructure.persistence.repository;

import com.autolift.targeting.infrastructure.persistence.entity.CustomerFeatureSnapshotJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerFeatureSnapshotJpaRepository
    extends JpaRepository<CustomerFeatureSnapshotJpaEntity, UUID> {

  List<CustomerFeatureSnapshotJpaEntity> findByCustomerId(String customerId);

  CustomerFeatureSnapshotJpaEntity findByCustomerIdAndCampaignId(
      String customerId, String campaignId);
}
