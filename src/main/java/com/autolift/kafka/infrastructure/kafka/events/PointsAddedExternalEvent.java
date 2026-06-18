package com.autolift.kafka.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.modulith.events.Externalized;

@Externalized("loyalty.points-added::#{#this.loyaltyAccountId.toString()}")
public record PointsAddedExternalEvent(
    UUID loyaltyAccountId, BigDecimal points, String referenceId) {}
