package com.autolift.kafka.infrastructure.persistence.repository;

import com.autolift.kafka.infrastructure.persistence.entity.ProcessedEventJpaEntity;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventJpaRepository
    extends JpaRepository<ProcessedEventJpaEntity, String> {

  boolean existsByEventId(String eventId);

  @Modifying
  @Query("DELETE FROM ProcessedEventJpaEntity p WHERE p.processedAt < :before")
  int deleteEventsProcessedBefore(@Param("before") Instant before);
}
