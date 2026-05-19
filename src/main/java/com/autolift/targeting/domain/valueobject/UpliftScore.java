package com.autolift.targeting.domain.valueobject;

import java.math.BigDecimal;

public record UpliftScore(BigDecimal value) {

  public static UpliftScore of(BigDecimal value) {
    return new UpliftScore(value);
  }

  public static UpliftScore from(double value) {
    return new UpliftScore(BigDecimal.valueOf(value));
  }

  public TargetingSegment toSegment() {
    if (value.compareTo(BigDecimal.valueOf(0.05)) >= 0) {
      return TargetingSegment.PERSUADABLE;
    } else if (value.compareTo(BigDecimal.valueOf(-0.01)) <= 0) {
      return TargetingSegment.DO_NOT_TARGET;
    } else {
      return TargetingSegment.NEUTRAL;
    }
  }
}
