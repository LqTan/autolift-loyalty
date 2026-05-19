package com.autolift.notification.domain.repository;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

  Notification save(Notification notification);

  Optional<Notification> findById(String id);

  List<Notification> findByStatus(NotificationStatus status);

  List<Notification> findByEventType(NotificationEventType eventType);

  List<Notification> findByRecipient(String recipient);

  List<Notification> findByCreatedAtBetween(Instant start, Instant end);

  long countByStatus(NotificationStatus status);
}
