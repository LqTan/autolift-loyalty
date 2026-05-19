package com.autolift.ml.domain.valueobject;

import java.util.UUID;
import lombok.Getter;

@Getter
public class ScheduledTaskLogId {
  private final UUID id;

  private ScheduledTaskLogId(UUID id) {
    this.id = id;
  }

  public static ScheduledTaskLogId of(UUID id) {
    return new ScheduledTaskLogId(id);
  }

  public static ScheduledTaskLogId of(String id) {
    return new ScheduledTaskLogId(UUID.fromString(id));
  }

  public static ScheduledTaskLogId random() {
    return new ScheduledTaskLogId(UUID.randomUUID());
  }
}
