package com.autolift.infrastructure.kafka.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PointsDeductedKafkaEvent(
    UUID loyaltyAccountId, BigDecimal points, String referenceId) {}
