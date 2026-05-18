package com.autolift.targeting.domain.valueobject;

public enum TargetingSegment {
  PERSUADABLE,
  NEUTRAL,
  DO_NOT_TARGET;

  public static TargetingSegment from(double upliftScore) {
    if (upliftScore >= 0.05) {
      return PERSUADABLE;
    } else if (upliftScore <= -0.01) {
      return DO_NOT_TARGET;
    } else {
      return NEUTRAL;
    }
  }
}