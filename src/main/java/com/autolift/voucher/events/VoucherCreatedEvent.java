package com.autolift.voucher.events;

import java.time.Instant;
import lombok.Getter;

@Getter
public class VoucherCreatedEvent {

  private final String voucherId;
  private final String code;
  private final String campaignId;
  private final Instant createdAt;

  public VoucherCreatedEvent(String voucherId, String code, String campaignId) {
    this.voucherId = voucherId;
    this.code = code;
    this.campaignId = campaignId;
    this.createdAt = Instant.now();
  }
}