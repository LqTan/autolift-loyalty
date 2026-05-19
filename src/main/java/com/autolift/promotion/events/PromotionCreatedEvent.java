package com.autolift.promotion.events;

import java.time.Instant;
import org.springframework.context.ApplicationEvent;

public class PromotionCreatedEvent extends ApplicationEvent {

  private final String promotionId;
  private final String name;
  private final Instant createdAt;

  public PromotionCreatedEvent(String promotionId, String name, Instant createdAt) {
    super(promotionId);
    this.promotionId = promotionId;
    this.name = name;
    this.createdAt = createdAt;
  }

  public String getPromotionId() {
    return promotionId;
  }

  public String getName() {
    return name;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
