package com.autolift.targeting.domain.valueobject;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UpliftScoreId {
  private final UUID id;
  private final Instant scoredAt;

  private UpliftScoreId(UUID id, Instant scoredAt) {
    this.id = id;
    this.scoredAt = scoredAt;
  }

  public static UpliftScoreId of(UUID id, Instant scoredAt) {
    return new UpliftScoreId(id, scoredAt);
  }

  public static UpliftScoreId of(String id, Instant scoredAt) {
    return new UpliftScoreId(UUID.fromString(id), scoredAt);
  }

  public static UpliftScoreId random() {
    return new UpliftScoreId(UUID.randomUUID(), Instant.now());
  }
}