package com.autolift.targeting.events;

import java.time.Instant;
import java.util.List;
import org.springframework.context.ApplicationEvent;

public class TargetCustomersSelectedEvent extends ApplicationEvent {

  private final String campaignId;
  private final List<String> customerIds;
  private final Instant selectedAt;

  public TargetCustomersSelectedEvent(
      String campaignId, List<String> customerIds, Instant selectedAt) {
    super(campaignId);
    this.campaignId = campaignId;
    this.customerIds = customerIds;
    this.selectedAt = selectedAt;
  }
}