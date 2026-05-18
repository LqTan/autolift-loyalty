package com.autolift.notification.application.command;

import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;

public record CreateNotificationCommand(
    NotificationEventType eventType,
    NotificationChannel channel,
    String recipient,
    String subject,
    String body,
    Map<String, Object> payload) {}