package com.autolift.loyalty.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class LoyaltyAccountId {

  private UUID id;

  private LoyaltyAccountId(UUID id) {
    this.id = id;
  }

  public static LoyaltyAccountId of(UUID id) {
    return new LoyaltyAccountId(id);
  }

  public static LoyaltyAccountId of(String id) {
    return new LoyaltyAccountId(UUID.fromString(id));
  }

  public static LoyaltyAccountId random() {
    return new LoyaltyAccountId(UUID.randomUUID());
  }
}
