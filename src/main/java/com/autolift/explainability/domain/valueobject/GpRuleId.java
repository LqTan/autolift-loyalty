package com.autolift.explainability.domain.valueobject;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class GpRuleId {

  private final UUID id;
  private final Instant createdAt;

  private GpRuleId(UUID id, Instant createdAt) {
    this.id = id;
    this.createdAt = createdAt;
  }

  public static GpRuleId of(UUID id, Instant createdAt) {
    return new GpRuleId(id, createdAt);
  }

  public static GpRuleId random() {
    return new GpRuleId(UUID.randomUUID(), Instant.now());
  }

  public UUID getId() {
    return id;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}