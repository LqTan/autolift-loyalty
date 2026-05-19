package com.autolift.ml.domain.model;

import com.autolift.ml.domain.valueobject.ScheduledTaskLogId;
import java.time.Instant;
import lombok.Getter;

@Getter
public class ScheduledTaskLog {

  private final ScheduledTaskLogId id;
  private final String taskName;
  private final String status;
  private final Instant startedAt;
  private final Instant completedAt;
  private final String errorMessage;

  protected ScheduledTaskLog() {
    this.id = null;
    this.taskName = null;
    this.status = null;
    this.startedAt = null;
    this.completedAt = null;
    this.errorMessage = null;
  }

  private ScheduledTaskLog(
      ScheduledTaskLogId id,
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

  public static ScheduledTaskLog of(
      ScheduledTaskLogId id,
      String taskName,
      String status,
      Instant startedAt,
      Instant completedAt,
      String errorMessage) {
    return new ScheduledTaskLog(id, taskName, status, startedAt, completedAt, errorMessage);
  }

  public static ScheduledTaskLog start(String taskName) {
    return new ScheduledTaskLog(
        ScheduledTaskLogId.random(), taskName, "RUNNING", Instant.now(), null, null);
  }

  public ScheduledTaskLog markCompleted() {
    return new ScheduledTaskLog(
        this.id, this.taskName, "COMPLETED", this.startedAt, Instant.now(), this.errorMessage);
  }

  public ScheduledTaskLog markFailed(String errorMessage) {
    return new ScheduledTaskLog(
        this.id, this.taskName, "FAILED", this.startedAt, Instant.now(), errorMessage);
  }

  public ScheduledTaskLogId getId() {
    return id;
  }

  public String getTaskName() {
    return taskName;
  }

  public String getStatus() {
    return status;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
