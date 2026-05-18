package com.autolift.ml.infrastructure.persistence.repository;

import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.ml.infrastructure.persistence.entity.MlJobJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

@Repository
public interface MlJobJpaRepository extends JpaRepository<MlJobJpaEntity, UUID> {

  List<MlJobJpaEntity> findByCampaignId(String campaignId);

  List<MlJobJpaEntity> findByStatus(MlJobStatus status);

  List<MlJobJpaEntity> findByJobTypeAndStatus(MlJobType jobType, MlJobStatus status);

  List<MlJobJpaEntity> findByUpliftScoreJobId(UUID upliftScoreJobId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT j FROM MlJobJpaEntity j WHERE j.jobType = :jobType AND j.status = :status ORDER BY j.createdAt ASC LIMIT 1")
  Optional<MlJobJpaEntity> findFirstPendingByJobTypeOrderByCreatedAtAsc(
      @Param("jobType") MlJobType jobType,
      @Param("status") MlJobStatus status);
}