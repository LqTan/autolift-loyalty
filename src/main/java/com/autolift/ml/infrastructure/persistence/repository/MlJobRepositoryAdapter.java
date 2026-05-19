package com.autolift.ml.infrastructure.persistence.repository;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.ml.infrastructure.persistence.mapper.MlJobMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MlJobRepositoryAdapter implements MlJobRepository {

  private final MlJobJpaRepository jpaRepository;

  public MlJobRepositoryAdapter(MlJobJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public MlJob save(MlJob job) {
    return MlJobMapper.toDomain(jpaRepository.save(MlJobMapper.toEntity(job)));
  }

  @Override
  public Optional<MlJob> findById(MlJobId id) {
    return jpaRepository.findById(id.getId()).map(MlJobMapper::toDomain);
  }

  @Override
  public List<MlJob> findByCampaignId(String campaignId) {
    return jpaRepository.findByCampaignId(campaignId).stream()
        .map(MlJobMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MlJob> findByStatus(MlJobStatus status) {
    return jpaRepository.findByStatus(status).stream()
        .map(MlJobMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MlJob> findByJobTypeAndStatus(MlJobType jobType, MlJobStatus status) {
    return jpaRepository.findByJobTypeAndStatus(jobType, status).stream()
        .map(MlJobMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MlJob> findByUpliftScoreJobId(UUID upliftScoreJobId) {
    return jpaRepository.findByUpliftScoreJobId(upliftScoreJobId).stream()
        .map(MlJobMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<MlJob> findFirstPendingByJobTypeOrderByCreatedAtAsc(MlJobType jobType) {
    return jpaRepository.findFirstPendingByJobTypeOrderByCreatedAtAsc(jobType, MlJobStatus.PENDING)
        .map(MlJobMapper::toDomain);
  }

  @Override
  public List<MlJob> findByCompletedAtBefore(Instant before) {
    return jpaRepository.findByCompletedAtBefore(before).stream()
        .map(MlJobMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public long countByStatus(MlJobStatus status) {
    return jpaRepository.countByStatus(status);
  }
}