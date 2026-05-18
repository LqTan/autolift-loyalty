package com.autolift.notification.infrastructure.persistence.repository;

import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import com.autolift.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {

  List<NotificationJpaEntity> findByStatus(NotificationStatus status);

  List<NotificationJpaEntity> findByEventType(NotificationEventType eventType);

  List<NotificationJpaEntity> findByRecipient(String recipient);

  List<NotificationJpaEntity> findByCreatedAtBetween(Instant start, Instant end);

  long countByStatus(NotificationStatus status);
}