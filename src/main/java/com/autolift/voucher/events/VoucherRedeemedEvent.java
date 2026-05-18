package com.autolift.voucher.events;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class VoucherRedeemedEvent extends ApplicationEvent {

  private final String voucherId;
  private final String code;
  private final String campaignId;
  private final String customerId;
  private final BigDecimal value;
  private final Instant redeemedAt;

  public VoucherRedeemedEvent(
      String voucherId,
      String code,
      String campaignId,
      String customerId,
      BigDecimal value) {
    super(voucherId);
    this.voucherId = voucherId;
    this.code = code;
    this.campaignId = campaignId;
    this.customerId = customerId;
    this.value = value;
    this.redeemedAt = Instant.now();
  }

  public String getVoucherId() { return voucherId; }
  public String getCode() { return code; }
  public String getCampaignId() { return campaignId; }
  public String getCustomerId() { return customerId; }
  public BigDecimal getValue() { return value; }
  public Instant getRedeemedAt() { return redeemedAt; }
}