package com.autolift.ml.domain.valueobject;

import java.util.UUID;
import lombok.Getter;

@Getter
public class MlJobId {
  private final UUID id;

  private MlJobId(UUID id) {
    this.id = id;
  }

  public static MlJobId of(UUID id) {
    return new MlJobId(id);
  }

  public static MlJobId of(String id) {
    return new MlJobId(UUID.fromString(id));
  }

  public static MlJobId random() {
    return new MlJobId(UUID.randomUUID());
  }
}