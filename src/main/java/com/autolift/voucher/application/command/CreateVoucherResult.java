package com.autolift.voucher.application.command;

import com.autolift.voucher.domain.valueobject.VoucherStatus;
import com.autolift.voucher.domain.valueobject.VoucherType;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherResult(
    String id,
    String code,
    String campaignId,
    VoucherType type,
    BigDecimal value,
    BigDecimal minOrderAmount,
    Integer maxUsage,
    Integer usedCount,
    VoucherStatus status,
    Instant validFrom,
    Instant validUntil) {}
