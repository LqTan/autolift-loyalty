package com.autolift.targeting.infrastructure.persistence.mapper;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.valueobject.TargetingSegment;
import com.autolift.targeting.domain.valueobject.UpliftScoreId;
import com.autolift.targeting.infrastructure.persistence.entity.CustomerUpliftScoreJpaEntity;

public class CustomerUpliftScoreMapper {

  public static CustomerUpliftScore toDomain(CustomerUpliftScoreJpaEntity entity) {
    return CustomerUpliftScore.of(
        UpliftScoreId.of(entity.getId(), entity.getScoredAt()),
        entity.getCustomerId(),
        entity.getCampaignId(),
        entity.getUpliftScore(),
        entity.getTreatmentProbability(),
        entity.getControlProbability(),
        TargetingSegment.valueOf(entity.getSegment()),
        entity.getModelVersion(),
        entity.getScoredAt());
  }

  public static CustomerUpliftScoreJpaEntity toEntity(CustomerUpliftScore domain) {
    return new CustomerUpliftScoreJpaEntity(
        domain.getId().getId(),
        domain.getCustomerId(),
        domain.getCampaignId(),
        domain.getUpliftScore(),
        domain.getTreatmentProbability(),
        domain.getControlProbability(),
        domain.getSegment().name(),
        domain.getModelVersion(),
        domain.getScoredAt());
  }
}
