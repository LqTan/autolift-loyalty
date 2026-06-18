package com.autolift.kafka.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("loyalty.points-deducted::#{#this.loyaltyAccountId.toString()}")
public record PointsDeductedExternalEvent(
    UUID loyaltyAccountId, BigDecimal points, String referenceId) {}
