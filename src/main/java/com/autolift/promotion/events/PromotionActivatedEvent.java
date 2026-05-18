package com.autolift.promotion.events;

import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class PromotionActivatedEvent extends ApplicationEvent {

  private final String promotionId;
  private final String name;
  private final Instant activatedAt;

  public PromotionActivatedEvent(String promotionId, String name, Instant activatedAt) {
    super(promotionId);
    this.promotionId = promotionId;
    this.name = name;
    this.activatedAt = activatedAt;
  }

  public String getPromotionId() { return promotionId; }
  public String getName() { return name; }
  public Instant getActivatedAt() { return activatedAt; }
}