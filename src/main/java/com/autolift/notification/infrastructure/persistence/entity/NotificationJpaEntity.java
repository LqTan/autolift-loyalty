package com.autolift.notification.infrastructure.persistence.entity;

import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notifications", schema = "notification")
public class NotificationJpaEntity {

  @Id private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private NotificationEventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel")
  private NotificationChannel channel;

  @Column(name = "recipient")
  private String recipient;

  @Column(name = "subject")
  private String subject;

  @Column(name = "body", columnDefinition = "TEXT")
  private String body;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private NotificationStatus status;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "payload", columnDefinition = "jsonb")
  private String payload;

  @Column(name = "error_message", columnDefinition = "TEXT")
  private String errorMessage;

  @Column(name = "sent_at")
  private Instant sentAt;

  @Column(name = "created_at")
  private Instant createdAt;

  public NotificationJpaEntity(
      UUID id,
      NotificationEventType eventType,
      NotificationChannel channel,
      String recipient,
      String subject,
      String body,
      NotificationStatus status,
      String payload,
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
}
