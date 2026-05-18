package com.autolift.notification.application.query;

import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetNotificationsHandler {

  private static final Logger log = LoggerFactory.getLogger(GetNotificationsHandler.class);

  private final NotificationRepository repository;

  public GetNotificationsHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<NotificationView> handle(GetNotificationsQuery query) {
    List<Notification> notifications;

    if (query.eventType() != null) {
      notifications = repository.findByEventType(query.eventType());
    } else if (query.status() != null) {
      notifications = repository.findByStatus(query.status());
    } else if (query.recipient() != null) {
      notifications = repository.findByRecipient(query.recipient());
    } else if (query.startDate() != null && query.endDate() != null) {
      notifications = repository.findByCreatedAtBetween(query.startDate(), query.endDate());
    } else {
      notifications = repository.findByStatus(NotificationStatus.PENDING);
    }

    return notifications.stream()
        .skip(query.offset())
        .limit(query.limit())
        .map(this::toView)
        .collect(Collectors.toList());
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