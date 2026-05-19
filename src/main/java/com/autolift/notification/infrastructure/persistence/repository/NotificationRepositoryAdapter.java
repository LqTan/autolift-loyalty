package com.autolift.notification.infrastructure.persistence.repository;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import com.autolift.notification.infrastructure.persistence.mapper.NotificationPersistenceMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationRepositoryAdapter implements NotificationRepository {

  private final NotificationJpaRepository jpaRepository;

  public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Notification save(Notification notification) {
    var entity = NotificationPersistenceMapper.toEntity(notification);
    var saved = jpaRepository.save(entity);
    return NotificationPersistenceMapper.toDomain(saved);
  }

  @Override
  public Optional<Notification> findById(String id) {
    return jpaRepository.findById(UUID.fromString(id)).map(NotificationPersistenceMapper::toDomain);
  }

  @Override
  public List<Notification> findByStatus(NotificationStatus status) {
    return jpaRepository.findByStatus(status).stream()
        .map(NotificationPersistenceMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findByEventType(NotificationEventType eventType) {
    return jpaRepository.findByEventType(eventType).stream()
        .map(NotificationPersistenceMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findByRecipient(String recipient) {
    return jpaRepository.findByRecipient(recipient).stream()
        .map(NotificationPersistenceMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findByCreatedAtBetween(Instant start, Instant end) {
    return jpaRepository.findByCreatedAtBetween(start, end).stream()
        .map(NotificationPersistenceMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public long countByStatus(NotificationStatus status) {
    return jpaRepository.countByStatus(status);
  }
}
