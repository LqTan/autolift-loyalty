package com.autolift.campaign.events;

import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class CampaignActivatedEvent extends ApplicationEvent {

  private final String campaignId;
  private final String name;
  private final Instant activatedAt;

  public CampaignActivatedEvent(String campaignId, String name, Instant activatedAt) {
    super(campaignId);
    this.campaignId = campaignId;
    this.name = name;
    this.activatedAt = activatedAt;
  }

  public String campaignId() {
    return campaignId;
  }

  public String name() {
    return name;
  }

  public Instant activatedAt() {
    return activatedAt;
  }
}
