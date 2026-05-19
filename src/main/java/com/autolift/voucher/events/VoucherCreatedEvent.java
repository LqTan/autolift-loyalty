package com.autolift.voucher.events;

import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class VoucherCreatedEvent extends ApplicationEvent {

  private final String voucherId;
  private final String code;
  private final String campaignId;
  private final Instant createdAt;

  public VoucherCreatedEvent(String voucherId, String code, String campaignId) {
    super(voucherId);
    this.voucherId = voucherId;
    this.code = code;
    this.campaignId = campaignId;
    this.createdAt = Instant.now();
  }
}
