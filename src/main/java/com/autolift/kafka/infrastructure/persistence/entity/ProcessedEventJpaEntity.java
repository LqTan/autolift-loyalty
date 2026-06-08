package com.autolift.kafka.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processed_events", schema = "kafka")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ProcessedEventJpaEntity {

  @Id
  @Column(name = "event_id")
  private String eventId;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "processed_at")
  private Instant processedAt;

  public ProcessedEventJpaEntity(String eventId, String eventType, Instant processedAt) {
    this.eventId = eventId;
    this.eventType = eventType;
    this.processedAt = processedAt;
  }

  public static ProcessedEventJpaEntity create(String eventId, String eventType) {
    return new ProcessedEventJpaEntity(eventId, eventType, Instant.now());
  }
}
