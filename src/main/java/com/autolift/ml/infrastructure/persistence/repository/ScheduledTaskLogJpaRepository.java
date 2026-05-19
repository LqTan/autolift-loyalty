package com.autolift.ml.infrastructure.persistence.repository;

import com.autolift.ml.infrastructure.persistence.entity.ScheduledTaskLogJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTaskLogJpaRepository extends JpaRepository<ScheduledTaskLogJpaEntity, UUID> {

  List<ScheduledTaskLogJpaEntity> findByTaskNameOrderByStartedAtDesc(String taskName);

  List<ScheduledTaskLogJpaEntity> findByStatusAndStartedAtBefore(String status, Instant before);
}