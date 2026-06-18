package com.autolift.kafka.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.modulith.events.Externalized;

@Externalized("voucher.redeemed::#{#this.voucherId}")
public record VoucherRedeemedExternalEvent(
    String voucherId,
    String code,
    String campaignId,
    String customerId,
    BigDecimal value,
    Instant redeemedAt) {}
