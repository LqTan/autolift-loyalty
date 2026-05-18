package com.autolift.notification.application.command;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarkNotificationSentHandler {

  private final NotificationRepository repository;

  public MarkNotificationSentHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Notification handle(String notificationId) {
    return repository.findById(notificationId)
        .map(n -> repository.save(n.markSent()))
        .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
  }
}