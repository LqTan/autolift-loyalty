package com.autolift.voucher.api.query;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherResponse(
    String id,
    String code,
    String campaignId,
    String type,
    BigDecimal value,
    BigDecimal minOrderAmount,
    Integer maxUsage,
    Integer usedCount,
    String status,
    Instant validFrom,
    Instant validUntil) {}
