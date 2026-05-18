package com.autolift.loyalty.events;

import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import java.math.BigDecimal;
import org.springframework.context.ApplicationEvent;

public class PointsDeductedEvent extends ApplicationEvent {

  private final LoyaltyAccountId loyaltyAccountId;
  private final BigDecimal points;
  private final String referenceId;

  public PointsDeductedEvent(
      LoyaltyAccountId loyaltyAccountId, BigDecimal points, String referenceId) {
    super(loyaltyAccountId);
    this.loyaltyAccountId = loyaltyAccountId;
    this.points = points;
    this.referenceId = referenceId;
  }
}