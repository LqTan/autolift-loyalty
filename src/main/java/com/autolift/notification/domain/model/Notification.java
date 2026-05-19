package com.autolift.notification.domain.model;

import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationId;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import java.time.Instant;
import java.util.Map;
import lombok.Getter;

@Getter
public class Notification {

  private final NotificationId id;
  private final NotificationEventType eventType;
  private final NotificationChannel channel;
  private final String recipient;
  private final String subject;
  private final String body;
  private final NotificationStatus status;
  private final Map<String, Object> payload;
  private final String errorMessage;
  private final Instant sentAt;
  private final Instant createdAt;

  protected Notification() {
    this.id = null;
    this.eventType = null;
    this.channel = null;
    this.recipient = null;
    this.subject = null;
    this.body = null;
    this.status = null;
    this.payload = null;
    this.errorMessage = null;
    this.sentAt = null;
    this.createdAt = null;
  }

  private Notification(
      NotificationId id,
      NotificationEventType eventType,
      NotificationChannel channel,
      String recipient,
      String subject,
      String body,
      NotificationStatus status,
      Map<String, Object> payload,
      String errorMessage,
      Instant sentAt,
      Instant createdAt) {
    this.id = id;
    this.eventType = eventType;
    this.channel = channel;
    this.recipient = recipient;
    this.subject = subject;
    this.body = body;
    this.status = status;
    this.payload = payload;
    this.errorMessage = errorMessage;
    this.sentAt = sentAt;
    this.createdAt = createdAt;
  }

  public static Notification create(
      NotificationEventType eventType,
      NotificationChannel channel,
      String recipient,
      String subject,
      String body,
      Map<String, Object> payload) {
    return new Notification(
        NotificationId.random(),
        eventType,
        channel,
        recipient,
        subject,
        body,
        NotificationStatus.PENDING,
        payload,
        null,
        null,
        Instant.now());
  }

  public static Notification of(
      NotificationId id,
      NotificationEventType eventType,
      NotificationChannel channel,
      String recipient,
      String subject,
      String body,
      NotificationStatus status,
      Map<String, Object> payload,
      String errorMessage,
      Instant sentAt,
      Instant createdAt) {
    return new Notification(
        id,
        eventType,
        channel,
        recipient,
        subject,
        body,
        status,
        payload,
        errorMessage,
        sentAt,
        createdAt);
  }

  public Notification markSent() {
    return new Notification(
        this.id,
        this.eventType,
        this.channel,
        this.recipient,
        this.subject,
        this.body,
        NotificationStatus.SENT,
        this.payload,
        this.errorMessage,
        Instant.now(),
        this.createdAt);
  }

  public Notification markFailed(String errorMessage) {
    return new Notification(
        this.id,
        this.eventType,
        this.channel,
        this.recipient,
        this.subject,
        this.body,
        NotificationStatus.FAILED,
        this.payload,
        errorMessage,
        this.sentAt,
        this.createdAt);
  }
}
