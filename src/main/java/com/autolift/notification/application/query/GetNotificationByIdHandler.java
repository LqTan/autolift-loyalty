package com.autolift.notification.application.query;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationByIdHandler {

  private final NotificationRepository repository;

  public GetNotificationByIdHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public NotificationView handle(String id) {
    return repository
        .findById(id)
        .map(this::toView)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
  }

  private NotificationView toView(Notification n) {
    return new NotificationView(
        n.getId().getId().toString(),
        n.getEventType().name(),
        n.getChannel().name(),
        n.getRecipient(),
        n.getSubject(),
        n.getBody(),
        n.getStatus().name(),
        n.getPayload(),
        n.getErrorMessage(),
        n.getSentAt(),
        n.getCreatedAt());
  }
}
