package com.autolift.notification.domain.exception;

public class NotificationNotFoundException extends RuntimeException {
  public NotificationNotFoundException(String id) {
    super("Notification not found: " + id);
  }
}
