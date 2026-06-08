package com.autolift.kafka.infrastructure.kafka.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record VoucherRedeemedKafkaEvent(
    String voucherId,
    String code,
    String campaignId,
    String customerId,
    BigDecimal value,
    Instant redeemedAt) {}
