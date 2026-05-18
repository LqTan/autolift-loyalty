package com.autolift.notification.infrastructure.persistence.mapper;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationId;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import com.autolift.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;

public class NotificationPersistenceMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static Notification toDomain(NotificationJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    Map<String, Object> payload = parsePayload(entity.getPayload());
    return Notification.of(
        NotificationId.of(entity.getId()),
        entity.getEventType(),
        entity.getChannel(),
        entity.getRecipient(),
        entity.getSubject(),
        entity.getBody(),
        entity.getStatus(),
        payload,
        entity.getErrorMessage(),
        entity.getSentAt(),
        entity.getCreatedAt());
  }

  public static NotificationJpaEntity toEntity(Notification notification) {
    if (notification == null) {
      return null;
    }
    String payloadJson = serializePayload(notification.getPayload());
    return new NotificationJpaEntity(
        notification.getId().getId(),
        notification.getEventType(),
        notification.getChannel(),
        notification.getRecipient(),
        notification.getSubject(),
        notification.getBody(),
        notification.getStatus(),
        payloadJson,
        notification.getErrorMessage(),
        notification.getSentAt(),
        notification.getCreatedAt());
  }

  private static Map<String, Object> parsePayload(String json) {
    if (json == null || json.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  private static String serializePayload(Map<String, Object> payload) {
    if (payload == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}