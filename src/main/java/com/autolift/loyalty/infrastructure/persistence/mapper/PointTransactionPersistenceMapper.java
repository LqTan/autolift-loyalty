package com.autolift.loyalty.infrastructure.persistence.mapper;

import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.model.PointTransaction.TransactionType;
import com.autolift.loyalty.domain.valueobject.PointTransactionId;
import com.autolift.loyalty.infrastructure.persistence.entity.PointTransactionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PointTransactionPersistenceMapper {

  public PointTransaction toDomain(PointTransactionJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return PointTransaction.of(
        PointTransactionId.of(entity.getId()),
        com.autolift.loyalty.domain.valueobject.PointTransactionId.of(entity.getLoyaltyAccountId()),
        entity.getAmount(),
        TransactionType.valueOf(entity.getTransactionType()),
        entity.getReferenceId(),
        entity.getCreatedAt());
  }

  public PointTransactionJpaEntity toEntity(PointTransaction domain) {
    if (domain == null) {
      return null;
    }
    return new PointTransactionJpaEntity(
        domain.getId().getId(),
        domain.getLoyaltyAccountId().getId(),
        domain.getAmount(),
        domain.getTransactionType().name(),
        domain.getReferenceId(),
        domain.getCreatedAt());
  }
}
