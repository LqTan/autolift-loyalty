package com.autolift.voucher.infrastructure.persistence.entity;

import com.autolift.voucher.domain.valueobject.VoucherStatus;
import com.autolift.voucher.domain.valueobject.VoucherType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vouchers", schema = "voucher")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class VoucherJpaEntity {

  @Id private UUID id;

  @Column(unique = true)
  private String code;

  @Column(name = "campaign_id")
  private String campaignId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  private VoucherType type;

  @Column private BigDecimal value;

  @Column(name = "min_order_amount")
  private BigDecimal minOrderAmount;

  @Column(name = "max_usage")
  private Integer maxUsage;

  @Column(name = "used_count")
  private Integer usedCount;

  @Enumerated(EnumType.STRING)
  @Column
  private VoucherStatus status;

  @Column(name = "valid_from")
  private Instant validFrom;

  @Column(name = "valid_until")
  private Instant validUntil;

  @Column(name = "created_at")
  private Instant createdAt;

  public VoucherJpaEntity(
      UUID id,
      String code,
      String campaignId,
      VoucherType type,
      BigDecimal value,
      BigDecimal minOrderAmount,
      Integer maxUsage,
      Integer usedCount,
      VoucherStatus status,
      Instant validFrom,
      Instant validUntil,
      Instant createdAt) {
    this.id = id;
    this.code = code;
    this.campaignId = campaignId;
    this.type = type;
    this.value = value;
    this.minOrderAmount = minOrderAmount;
    this.maxUsage = maxUsage;
    this.usedCount = usedCount;
    this.status = status;
    this.validFrom = validFrom;
    this.validUntil = validUntil;
    this.createdAt = createdAt;
  }

  public static VoucherJpaEntity of(
      UUID id,
      String code,
      String campaignId,
      VoucherType type,
      BigDecimal value,
      BigDecimal minOrderAmount,
      Integer maxUsage,
      Integer usedCount,
      VoucherStatus status,
      Instant validFrom,
      Instant validUntil,
      Instant createdAt) {
    return new VoucherJpaEntity(
        id,
        code,
        campaignId,
        type,
        value,
        minOrderAmount,
        maxUsage,
        usedCount,
        status,
        validFrom,
        validUntil,
        createdAt);
  }
}
