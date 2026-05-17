package com.autolift.voucher.application.command;

public record RedeemVoucherCommand(String code, String customerId) {}