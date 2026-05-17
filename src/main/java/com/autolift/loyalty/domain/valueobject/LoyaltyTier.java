package com.autolift.loyalty.domain.valueobject;

import java.math.BigDecimal;

public enum LoyaltyTier {
  BRONZE,
  SILVER,
  GOLD,
  PLATINUM;

  public static LoyaltyTier fromPoints(BigDecimal points) {
    if (points.compareTo(new BigDecimal("10000")) >= 0) {
      return PLATINUM;
    } else if (points.compareTo(new BigDecimal("5000")) >= 0) {
      return GOLD;
    } else if (points.compareTo(new BigDecimal("1000")) >= 0) {
      return SILVER;
    } else {
      return BRONZE;
    }
  }

  public LoyaltyTier upgrade(BigDecimal points) {
    LoyaltyTier newTier = fromPoints(points);
    if (newTier.ordinal() > this.ordinal()) {
      return newTier;
    }
    return this;
  }
}