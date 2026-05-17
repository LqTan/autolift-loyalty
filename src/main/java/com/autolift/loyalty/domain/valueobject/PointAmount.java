package com.autolift.loyalty.domain.valueobject;

import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PointAmount {

  private BigDecimal value;

  private PointAmount(BigDecimal value) {
    this.value = value;
  }

  public static PointAmount of(BigDecimal value) {
    return new PointAmount(value);
  }

  public static PointAmount zero() {
    return new PointAmount(BigDecimal.ZERO);
  }

  public boolean isPositive() {
    return value.compareTo(BigDecimal.ZERO) > 0;
  }

  public boolean isNegative() {
    return value.compareTo(BigDecimal.ZERO) < 0;
  }

  public boolean isZero() {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }
}