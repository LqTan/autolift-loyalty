package com.autolift.infrastructure.kafka.events;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.context.ApplicationEvent;

public class PointsAddedInternalEvent extends ApplicationEvent {

  private final UUID loyaltyAccountId;
  private final BigDecimal points;
  private final String referenceId;

  public PointsAddedInternalEvent(UUID loyaltyAccountId, BigDecimal points, String referenceId) {
    super(loyaltyAccountId);
    this.loyaltyAccountId = loyaltyAccountId;
    this.points = points;
    this.referenceId = referenceId;
  }

  public UUID getLoyaltyAccountId() {
    return loyaltyAccountId;
  }

  public BigDecimal getPoints() {
    return points;
  }

  public String getReferenceId() {
    return referenceId;
  }
}
