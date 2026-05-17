package com.autolift.loyalty.api.command;

import java.math.BigDecimal;

public record UsePointsRequest(
    BigDecimal amount,
    String referenceId) {}