package com.autolift.loyalty.api.command;

import java.math.BigDecimal;

public record AddPointsRequest(
    BigDecimal amount,
    String referenceId) {}