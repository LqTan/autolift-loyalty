package com.autolift.notification.domain.valueobject;

import java.util.UUID;
import lombok.Getter;

@Getter
public class NotificationId {

  private final UUID id;

  private NotificationId(UUID id) {
    this.id = id;
  }

  public static NotificationId of(UUID id) {
    return new NotificationId(id);
  }

  public static NotificationId random() {
    return new NotificationId(UUID.randomUUID());
  }
}