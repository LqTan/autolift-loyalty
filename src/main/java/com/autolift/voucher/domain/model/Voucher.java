package com.autolift.voucher.domain.model;

import com.autolift.voucher.domain.valueobject.VoucherId;
import com.autolift.voucher.domain.valueobject.VoucherStatus;
import com.autolift.voucher.domain.valueobject.VoucherType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Voucher {

  private final VoucherId id;
  private final String code;
  private final String campaignId;
  private final VoucherType type;
  private final BigDecimal value;
  private final BigDecimal minOrderAmount;
  private final Integer maxUsage;
  private Integer usedCount;
  private VoucherStatus status;
  private final Instant validFrom;
  private final Instant validUntil;
  private final Instant createdAt;

  protected Voucher() {
    this.id = null;
    this.code = null;
    this.campaignId = null;
    this.type = null;
    this.value = null;
    this.minOrderAmount = null;
    this.maxUsage = null;
    this.usedCount = null;
    this.status = null;
    this.validFrom = null;
    this.validUntil = null;
    this.createdAt = null;
  }

  private Voucher(
      VoucherId id,
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

  public static Voucher create(
      String code,
      String campaignId,
      VoucherType type,
      BigDecimal value,
      BigDecimal minOrderAmount,
      Integer maxUsage,
      Instant validFrom,
      Instant validUntil) {
    VoucherId id = VoucherId.random();
    Instant now = Instant.now();
    return new Voucher(
        id,
        code,
        campaignId,
        type,
        value,
        minOrderAmount,
        maxUsage,
        0,
        VoucherStatus.ACTIVE,
        validFrom,
        validUntil,
        now);
  }

  public static Voucher of(
      VoucherId id,
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
    return new Voucher(
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

  public void redeem() {
    if (this.status != VoucherStatus.ACTIVE) {
      throw new IllegalStateException("Cannot redeem voucher with status: " + this.status);
    }
    if (this.maxUsage != null && this.usedCount >= this.maxUsage) {
      throw new IllegalStateException("Voucher usage limit reached");
    }
    Instant now = Instant.now();
    if (this.validUntil != null && now.isAfter(this.validUntil)) {
      throw new IllegalStateException("Voucher has expired");
    }
    this.usedCount++;
    if (this.maxUsage != null && this.usedCount >= this.maxUsage) {
      this.status = VoucherStatus.USED;
    }
  }

  public void cancel() {
    if (this.status == VoucherStatus.USED) {
      throw new IllegalStateException("Cannot cancel a used voucher");
    }
    this.status = VoucherStatus.CANCELLED;
  }

  public void expire() {
    if (this.status == VoucherStatus.USED || this.status == VoucherStatus.CANCELLED) {
      return;
    }
    this.status = VoucherStatus.EXPIRED;
  }

  public boolean isValid() {
    Instant now = Instant.now();
    return status == VoucherStatus.ACTIVE
        && (validFrom == null || now.isAfter(validFrom))
        && (validUntil == null || now.isBefore(validUntil))
        && (maxUsage == null || usedCount < maxUsage);
  }
}
