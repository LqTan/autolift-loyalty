package com.autolift.voucher.api.command;

import com.autolift.voucher.domain.valueobject.VoucherType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateVoucherRequest(
    @NotBlank String code,
    String campaignId,
    String type,
    @DecimalMin("0") BigDecimal value,
    @DecimalMin("0") BigDecimal minOrderAmount,
    Integer maxUsage,
    Instant validFrom,
    Instant validUntil) {

  public VoucherType voucherType() {
    if (type == null) {
      return VoucherType.DISCOUNT_PERCENTAGE;
    }
    return switch (type.toUpperCase()) {
      case "PERCENTAGE", "DISCOUNT_PERCENTAGE" -> VoucherType.DISCOUNT_PERCENTAGE;
      case "FIXED", "DISCOUNT_FIXED_AMOUNT" -> VoucherType.DISCOUNT_FIXED_AMOUNT;
      case "FREE_SHIPPING", "SHIPPING" -> VoucherType.FREE_SHIPPING;
      case "BUY_X_GET_Y", "BUY_X" -> VoucherType.BUY_X_GET_Y;
      default -> VoucherType.DISCOUNT_PERCENTAGE;
    };
  }
}
