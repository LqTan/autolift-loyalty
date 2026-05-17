package com.autolift.loyalty.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PointTransactionId {

  private UUID id;

  private PointTransactionId(UUID id) {
    this.id = id;
  }

  public static PointTransactionId of(UUID id) {
    return new PointTransactionId(id);
  }

  public static PointTransactionId of(String id) {
    return new PointTransactionId(UUID.fromString(id));
  }

  public static PointTransactionId random() {
    return new PointTransactionId(UUID.randomUUID());
  }
}