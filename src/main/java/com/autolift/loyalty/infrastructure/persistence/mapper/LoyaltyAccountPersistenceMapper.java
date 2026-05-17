package com.autolift.loyalty.infrastructure.persistence.mapper;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountStatus;
import com.autolift.loyalty.domain.valueobject.LoyaltyTier;
import com.autolift.loyalty.infrastructure.persistence.entity.LoyaltyAccountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyAccountPersistenceMapper {

  public LoyaltyAccount toDomain(LoyaltyAccountJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return LoyaltyAccount.of(
        LoyaltyAccountId.of(entity.getId()),
        entity.getCustomerId(),
        entity.getPointsBalance(),
        LoyaltyTier.valueOf(entity.getTier()),
        LoyaltyAccountStatus.valueOf(entity.getStatus()),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public LoyaltyAccountJpaEntity toEntity(LoyaltyAccount domain) {
    if (domain == null) {
      return null;
    }
    return new LoyaltyAccountJpaEntity(
        domain.getId().getId(),
        domain.getCustomerId(),
        domain.getPointsBalance(),
        domain.getTier().name(),
        domain.getStatus().name(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }
}