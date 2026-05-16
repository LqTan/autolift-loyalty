package com.autolift.campaign.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
public class Budget {

    private final BigDecimal amount;
    private final String currency;

    private Budget(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Budget of(BigDecimal amount, String currency) {
        return new Budget(amount, currency);
    }

    public static Budget of(BigDecimal amount) {
        return new Budget(amount, "VND");
    }
}