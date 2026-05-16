package com.autolift.promotion.events;

import java.time.Instant;
import lombok.Getter;

@Getter
public class PromotionCreatedEvent {

  private final String promotionId;
  private final String name;
  private final Instant createdAt;

  public PromotionCreatedEvent(String promotionId, String name, Instant createdAt) {
    this.promotionId = promotionId;
    this.name = name;
    this.createdAt = createdAt;
  }
}
