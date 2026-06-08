package com.autolift.kafka.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_events", schema = "kafka")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class OutboxEventJpaEntity {

  public enum Status {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
  }

  @Id private UUID id;

  @Column(name = "aggregate_type")
  private String aggregateType;

  @Column(name = "event_type")
  private String eventType;

  @Column private String topic;

  @Column(name = "event_key")
  private String eventKey;

  @Column private String payload;

  @Enumerated(EnumType.STRING)
  @Column
  private Status status;

  @Column(name = "retry_count")
  private Integer retryCount;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "processed_at")
  private Instant processedAt;

  @Column(name = "last_error")
  private String lastError;

  public OutboxEventJpaEntity(
      UUID id,
      String aggregateType,
      String eventType,
      String topic,
      String eventKey,
      String payload,
      Status status,
      Integer retryCount,
      Instant createdAt,
      Instant processedAt,
      String lastError) {
    this.id = id;
    this.aggregateType = aggregateType;
    this.eventType = eventType;
    this.topic = topic;
    this.eventKey = eventKey;
    this.payload = payload;
    this.status = status;
    this.retryCount = retryCount;
    this.createdAt = createdAt;
    this.processedAt = processedAt;
    this.lastError = lastError;
  }

  public static OutboxEventJpaEntity create(
      String aggregateType, String eventType, String topic, String eventKey, String payload) {
    return new OutboxEventJpaEntity(
        UUID.randomUUID(),
        aggregateType,
        eventType,
        topic,
        eventKey,
        payload,
        Status.PENDING,
        0,
        Instant.now(),
        null,
        null);
  }

  public void markProcessing() {
    this.status = Status.PROCESSING;
  }

  public void markCompleted() {
    this.status = Status.COMPLETED;
    this.processedAt = Instant.now();
  }

  public void markFailed(String error) {
    this.status = Status.FAILED;
    this.lastError = error;
    this.retryCount++;
  }

  public boolean canRetry(int maxRetries) {
    return this.retryCount < maxRetries && this.status == Status.FAILED;
  }
}
