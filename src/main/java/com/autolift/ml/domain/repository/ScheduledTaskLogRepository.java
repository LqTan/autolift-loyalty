package com.autolift.ml.domain.repository;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ScheduledTaskLogRepository {

  ScheduledTaskLog save(ScheduledTaskLog log);

  Optional<ScheduledTaskLog> findById(java.util.UUID id);

  List<ScheduledTaskLog> findByTaskNameOrderByStartedAtDesc(String taskName);

  List<ScheduledTaskLog> findByStatusAndStartedAtBefore(String status, Instant before);

  void deleteById(java.util.UUID id);
}
