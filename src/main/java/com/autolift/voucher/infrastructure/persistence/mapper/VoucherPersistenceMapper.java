package com.autolift.voucher.infrastructure.persistence.mapper;

import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.valueobject.VoucherId;
import com.autolift.voucher.infrastructure.persistence.entity.VoucherJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class VoucherPersistenceMapper {

  public Voucher toDomain(VoucherJpaEntity entity) {
    if (entity == null) return null;
    return Voucher.of(
        VoucherId.of(entity.getId()),
        entity.getCode(),
        entity.getCampaignId(),
        entity.getType(),
        entity.getValue(),
        entity.getMinOrderAmount(),
        entity.getMaxUsage(),
        entity.getUsedCount(),
        entity.getStatus(),
        entity.getValidFrom(),
        entity.getValidUntil(),
        entity.getCreatedAt());
  }

  public VoucherJpaEntity toEntity(Voucher domain) {
    if (domain == null) return null;
    return VoucherJpaEntity.of(
        domain.getId().getId(),
        domain.getCode(),
        domain.getCampaignId(),
        domain.getType(),
        domain.getValue(),
        domain.getMinOrderAmount(),
        domain.getMaxUsage(),
        domain.getUsedCount(),
        domain.getStatus(),
        domain.getValidFrom(),
        domain.getValidUntil(),
        domain.getCreatedAt());
  }
}