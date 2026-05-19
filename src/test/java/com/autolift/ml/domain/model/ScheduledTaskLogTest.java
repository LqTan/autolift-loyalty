package com.autolift.ml.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.autolift.ml.domain.valueobject.ScheduledTaskLogId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ScheduledTaskLogTest {

  @Test
  void shouldStartTaskLog() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start("cleanup");

    assertNotNull(taskLog.getId());
    assertEquals("cleanup", taskLog.getTaskName());
    assertEquals("RUNNING", taskLog.getStatus());
    assertNotNull(taskLog.getStartedAt());
    assertNull(taskLog.getCompletedAt());
    assertNull(taskLog.getErrorMessage());
  }

  @Test
  void shouldMarkCompleted() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start("cleanup");
    ScheduledTaskLog completed = taskLog.markCompleted();

    assertEquals("COMPLETED", completed.getStatus());
    assertNotNull(completed.getCompletedAt());
    assertNull(completed.getErrorMessage());
  }

  @Test
  void shouldMarkFailed() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start("cleanup");
    ScheduledTaskLog failed = taskLog.markFailed("Something went wrong");

    assertEquals("FAILED", failed.getStatus());
    assertNotNull(failed.getCompletedAt());
    assertEquals("Something went wrong", failed.getErrorMessage());
  }

  @Test
  void shouldCreateFromFactory() {
    ScheduledTaskLogId id = ScheduledTaskLogId.random();
    Instant now = Instant.now();
    ScheduledTaskLog taskLog = ScheduledTaskLog.of(id, "report", "COMPLETED", now, now, null);

    assertEquals(id, taskLog.getId());
    assertEquals("report", taskLog.getTaskName());
    assertEquals("COMPLETED", taskLog.getStatus());
  }
}