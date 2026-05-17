package com.autolift.voucher.application.command;

import com.autolift.voucher.domain.valueobject.VoucherType;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherCommand(
    String code,
    String campaignId,
    VoucherType type,
    BigDecimal value,
    BigDecimal minOrderAmount,
    Integer maxUsage,
    Instant validFrom,
    Instant validUntil) {}