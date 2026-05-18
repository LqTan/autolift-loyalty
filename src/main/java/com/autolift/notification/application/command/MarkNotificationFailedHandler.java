package com.autolift.notification.application.command;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkNotificationFailedHandler {

  private final NotificationRepository repository;

  public MarkNotificationFailedHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Notification handle(String notificationId, String errorMessage) {
    return repository.findById(notificationId)
        .map(n -> repository.save(n.markFailed(errorMessage)))
        .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
  }
}