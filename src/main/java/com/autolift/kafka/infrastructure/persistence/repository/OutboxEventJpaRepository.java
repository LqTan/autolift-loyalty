package com.autolift.kafka.infrastructure.persistence.repository;

import com.autolift.kafka.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.autolift.kafka.infrastructure.persistence.entity.OutboxEventJpaEntity.Status;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

  List<OutboxEventJpaEntity> findByStatusOrderByCreatedAtAsc(Status status);

  @Query(
      "SELECT o FROM OutboxEventJpaEntity o WHERE o.status = :status ORDER BY o.createdAt ASC LIMIT :limit")
  List<OutboxEventJpaEntity> findByStatusOrderByCreatedAtAscWithLimit(
      @Param("status") Status status, @Param("limit") int limit);

  @Modifying
  @Query(
      "DELETE FROM OutboxEventJpaEntity o WHERE o.status = 'COMPLETED' AND o.processedAt < :before")
  int deleteCompletedEventsBefore(@Param("before") Instant before);

  @Query(
      "SELECT COUNT(o) FROM OutboxEventJpaEntity o WHERE o.status = 'FAILED' AND o.retryCount < :maxRetries")
  long countRetryableEvents(@Param("maxRetries") int maxRetries);
}
