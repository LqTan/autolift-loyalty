package com.autolift.ml.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "scheduled_tasks_log", schema = "ml")
public class ScheduledTaskLogJpaEntity {

  @Id private UUID id;

  @Column(name = "task_name", nullable = false)
  private String taskName;

  @Column(nullable = false)
  private String status;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(name = "error_message")
  private String errorMessage;

  public ScheduledTaskLogJpaEntity(
      UUID id,
      String taskName,
      String status,
      Instant startedAt,
      Instant completedAt,
      String errorMessage) {
    this.id = id;
    this.taskName = taskName;
    this.status = status;
    this.startedAt = startedAt;
    this.completedAt = completedAt;
    this.errorMessage = errorMessage;
  }
}
