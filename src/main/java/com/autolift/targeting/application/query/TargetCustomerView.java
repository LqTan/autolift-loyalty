package com.autolift.targeting.application.query;

import com.autolift.targeting.domain.valueobject.TargetingSegment;
import java.math.BigDecimal;

public record TargetCustomerView(
    String customerId,
    BigDecimal upliftScore,
    TargetingSegment segment,
    BigDecimal treatmentProbability,
    BigDecimal controlProbability) {}
