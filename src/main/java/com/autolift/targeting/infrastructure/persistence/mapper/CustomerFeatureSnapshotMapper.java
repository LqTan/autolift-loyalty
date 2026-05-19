package com.autolift.targeting.infrastructure.persistence.mapper;

import com.autolift.targeting.domain.model.CustomerFeatureSnapshot;
import com.autolift.targeting.infrastructure.persistence.entity.CustomerFeatureSnapshotJpaEntity;

public class CustomerFeatureSnapshotMapper {

  public static CustomerFeatureSnapshot toDomain(CustomerFeatureSnapshotJpaEntity entity) {
    return CustomerFeatureSnapshot.of(
        entity.getId(),
        entity.getCustomerId(),
        entity.getCampaignId(),
        entity.getRecencyDays(),
        entity.getFrequency90d(),
        entity.getMonetary90d(),
        entity.getAvgBasketValue(),
        entity.getTotalQuantity90d(),
        entity.getUniqueProductCount(),
        entity.getUniqueCategoryCount(),
        entity.getFavoriteCategory(),
        entity.getFeatureVersion(),
        entity.getCreatedAt());
  }

  public static CustomerFeatureSnapshotJpaEntity toEntity(CustomerFeatureSnapshot domain) {
    return new CustomerFeatureSnapshotJpaEntity(
        domain.getId(),
        domain.getCustomerId(),
        domain.getCampaignId(),
        domain.getRecencyDays(),
        domain.getFrequency90d(),
        domain.getMonetary90d(),
        domain.getAvgBasketValue(),
        domain.getTotalQuantity90d(),
        domain.getUniqueProductCount(),
        domain.getUniqueCategoryCount(),
        domain.getFavoriteCategory(),
        domain.getFeatureVersion(),
        domain.getCreatedAt());
  }
}
