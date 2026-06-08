package com.autolift.kafka.infrastructure.kafka.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PointsAddedKafkaEvent(UUID loyaltyAccountId, BigDecimal points, String referenceId) {}
