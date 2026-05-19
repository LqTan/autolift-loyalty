package com.autolift.ml.infrastructure.persistence.repository;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
import com.autolift.ml.infrastructure.persistence.mapper.ScheduledTaskLogMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduledTaskLogRepositoryAdapter implements ScheduledTaskLogRepository {

  private final ScheduledTaskLogJpaRepository jpaRepository;

  public ScheduledTaskLogRepositoryAdapter(ScheduledTaskLogJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public ScheduledTaskLog save(ScheduledTaskLog log) {
    return ScheduledTaskLogMapper.toDomain(
        jpaRepository.save(ScheduledTaskLogMapper.toEntity(log)));
  }

  @Override
  public Optional<ScheduledTaskLog> findById(UUID id) {
    return jpaRepository.findById(id).map(ScheduledTaskLogMapper::toDomain);
  }

  @Override
  public List<ScheduledTaskLog> findByTaskNameOrderByStartedAtDesc(String taskName) {
    return jpaRepository.findByTaskNameOrderByStartedAtDesc(taskName).stream()
        .map(ScheduledTaskLogMapper::toDomain)
        .toList();
  }

  @Override
  public List<ScheduledTaskLog> findByStatusAndStartedAtBefore(String status, Instant before) {
    return jpaRepository.findByStatusAndStartedAtBefore(status, before).stream()
        .map(ScheduledTaskLogMapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }
}
