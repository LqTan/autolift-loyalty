package com.autolift.infrastructure.kafka.events;

import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class CampaignActivatedInternalEvent extends ApplicationEvent {

  private final String campaignId;
  private final String name;
  private final Instant activatedAt;

  public CampaignActivatedInternalEvent(String campaignId, String name, Instant activatedAt) {
    super(campaignId);
    this.campaignId = campaignId;
    this.name = name;
    this.activatedAt = activatedAt;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public String getName() {
    return name;
  }

  public Instant getActivatedAt() {
    return activatedAt;
  }
}
