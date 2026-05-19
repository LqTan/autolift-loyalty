package com.autolift.loyalty.application.command;

import java.math.BigDecimal;

public record UsePointsCommand(String accountId, BigDecimal amount, String referenceId) {}
