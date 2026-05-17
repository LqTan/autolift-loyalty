package com.autolift.voucher.events;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class VoucherRedeemedEvent {

  private final String voucherId;
  private final String code;
  private final String campaignId;
  private final String customerId;
  private final BigDecimal value;
  private final Instant redeemedAt;

  public VoucherRedeemedEvent(String voucherId, String code, String campaignId, String customerId, BigDecimal value) {
    this.voucherId = voucherId;
    this.code = code;
    this.campaignId = campaignId;
    this.customerId = customerId;
    this.value = value;
    this.redeemedAt = Instant.now();
  }
}