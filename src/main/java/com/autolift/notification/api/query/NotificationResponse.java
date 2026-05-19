package com.autolift.notification.api.query;

import java.time.Instant;
import java.util.Map;

public record NotificationResponse(
    String id,
    String eventType,
    String channel,
    String recipient,
    String subject,
    String body,
    String status,
    Map<String, Object> payload,
    String errorMessage,
    Instant sentAt,
    Instant createdAt) {}
