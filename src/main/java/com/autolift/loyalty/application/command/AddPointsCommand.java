package com.autolift.loyalty.application.command;

import java.math.BigDecimal;

public record AddPointsCommand(
    String accountId,
    BigDecimal amount,
    String referenceId) {}