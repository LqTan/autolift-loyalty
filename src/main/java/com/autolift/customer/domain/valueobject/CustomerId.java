package com.autolift.customer.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CustomerId {

  private UUID id;

  private CustomerId(UUID id) {
    this.id = id;
  }

  public static CustomerId of(UUID id) {
    return new CustomerId(id);
  }

  public static CustomerId of(String id) {
    return new CustomerId(UUID.fromString(id));
  }

  public static CustomerId random() {
    return new CustomerId(UUID.randomUUID());
  }
}
