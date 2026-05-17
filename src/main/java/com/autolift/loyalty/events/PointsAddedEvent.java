package com.autolift.loyalty.events;

import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class PointsAddedEvent {

  private final LoyaltyAccountId loyaltyAccountId;
  private final BigDecimal points;
  private final String referenceId;

  public PointsAddedEvent(LoyaltyAccountId loyaltyAccountId, BigDecimal points, String referenceId) {
    this.loyaltyAccountId = loyaltyAccountId;
    this.points = points;
    this.referenceId = referenceId;
  }
}