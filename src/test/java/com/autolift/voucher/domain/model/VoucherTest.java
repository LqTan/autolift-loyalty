package com.autolift.voucher.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.autolift.voucher.domain.valueobject.VoucherStatus;
import com.autolift.voucher.domain.valueobject.VoucherType;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class VoucherTest {

  @Test
  void shouldCreateVoucher() {
    Voucher voucher =
        Voucher.create(
            "VCHR001",
            null,
            VoucherType.DISCOUNT_PERCENTAGE,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    assertNotNull(voucher.getId());
    assertEquals("VCHR001", voucher.getCode());
    assertEquals(VoucherType.DISCOUNT_PERCENTAGE, voucher.getType());
    assertEquals(new BigDecimal("10"), voucher.getValue());
    assertEquals(0, voucher.getUsedCount());
    assertEquals(VoucherStatus.ACTIVE, voucher.getStatus());
  }

  @Test
  void shouldRedeemVoucher() {
    Voucher voucher =
        Voucher.create(
            "VCHR001",
            null,
            VoucherType.DISCOUNT_PERCENTAGE,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    voucher.redeem();

    assertEquals(1, voucher.getUsedCount());
  }

  @Test
  void shouldNotRedeemVoucherWhenMaxUsageReached() {
    Voucher voucher =
        Voucher.create(
            "VCHR001",
            null,
            VoucherType.DISCOUNT_PERCENTAGE,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            1,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    voucher.redeem();

    assertThrows(IllegalStateException.class, () -> voucher.redeem());
  }

  @Test
  void shouldExpireVoucher() {
    Voucher voucher =
        Voucher.create(
            "VCHR001",
            null,
            VoucherType.DISCOUNT_PERCENTAGE,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    voucher.expire();

    assertEquals(VoucherStatus.EXPIRED, voucher.getStatus());
  }
}
