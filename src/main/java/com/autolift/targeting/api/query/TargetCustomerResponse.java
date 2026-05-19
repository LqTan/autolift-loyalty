package com.autolift.targeting.api.query;

import com.autolift.targeting.domain.valueobject.TargetingSegment;
import java.math.BigDecimal;

public record TargetCustomerResponse(
    String customerId,
    BigDecimal upliftScore,
    TargetingSegment segment,
    BigDecimal treatmentProbability,
    BigDecimal controlProbability) {}
