package com.autolift.promotion.events;

import java.time.Instant;
import lombok.Getter;

@Getter
public class PromotionActivatedEvent {

  private final String promotionId;
  private final String name;
  private final Instant activatedAt;

  public PromotionActivatedEvent(String promotionId, String name, Instant activatedAt) {
    this.promotionId = promotionId;
    this.name = name;
    this.activatedAt = activatedAt;
  }
}
