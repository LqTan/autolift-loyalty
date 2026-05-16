package com.autolift.promotion.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PromotionId {

  private UUID id;

  private PromotionId(UUID id) {
    this.id = id;
  }

  public static PromotionId of(UUID id) {
    return new PromotionId(id);
  }

  public static PromotionId of(String id) {
    return new PromotionId(UUID.fromString(id));
  }

  public static PromotionId random() {
    return new PromotionId(UUID.randomUUID());
  }
}
