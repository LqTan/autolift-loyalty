package com.autolift.voucher.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class VoucherId {

  private UUID id;

  private VoucherId(UUID id) {
    this.id = id;
  }

  public static VoucherId of(UUID id) {
    return new VoucherId(id);
  }

  public static VoucherId of(String id) {
    return new VoucherId(UUID.fromString(id));
  }

  public static VoucherId random() {
    return new VoucherId(UUID.randomUUID());
  }
}
