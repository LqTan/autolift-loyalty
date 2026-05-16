package com.autolift.promotion.infrastructure.persistence.mapper;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.infrastructure.persistence.entity.PromotionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PromotionPersistenceMapper {

  public Promotion toDomain(PromotionJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return Promotion.of(
        PromotionId.of(entity.getId()),
        entity.getName(),
        entity.getDescription(),
        entity.getPromotionType(),
        entity.getValue(),
        entity.getMinOrderAmount(),
        entity.getApplicableCustomerSegment(),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public PromotionJpaEntity toEntity(Promotion promotion) {
    if (promotion == null) {
      return null;
    }
    return PromotionJpaEntity.of(
        promotion.getId().getId(),
        promotion.getName(),
        promotion.getDescription(),
        promotion.getPromotionType(),
        promotion.getValue(),
        promotion.getMinOrderAmount(),
        promotion.getApplicableCustomerSegment(),
        promotion.getStatus(),
        promotion.getStartDate(),
        promotion.getEndDate(),
        promotion.getCreatedAt(),
        promotion.getUpdatedAt());
  }
}
