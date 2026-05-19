package com.autolift.ml.infrastructure.persistence.mapper;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.valueobject.ScheduledTaskLogId;
import com.autolift.ml.infrastructure.persistence.entity.ScheduledTaskLogJpaEntity;

public class ScheduledTaskLogMapper {

  public static ScheduledTaskLog toDomain(ScheduledTaskLogJpaEntity entity) {
    return ScheduledTaskLog.of(
        ScheduledTaskLogId.of(entity.getId()),
        entity.getTaskName(),
        entity.getStatus(),
        entity.getStartedAt(),
        entity.getCompletedAt(),
        entity.getErrorMessage());
  }

  public static ScheduledTaskLogJpaEntity toEntity(ScheduledTaskLog domain) {
    return new ScheduledTaskLogJpaEntity(
        domain.getId().getId(),
        domain.getTaskName(),
        domain.getStatus(),
        domain.getStartedAt(),
        domain.getCompletedAt(),
        domain.getErrorMessage());
  }
}
