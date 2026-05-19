package com.autolift.explainability.infrastructure.persistence.repository;

import com.autolift.explainability.infrastructure.persistence.entity.GpRuleJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GpRuleJpaRepository extends JpaRepository<GpRuleJpaEntity, UUID> {

  List<GpRuleJpaEntity> findByCampaignId(String campaignId);

  @Query("SELECT g FROM GpRuleJpaEntity g WHERE g.campaignId = :campaignId ORDER BY g.f1Score DESC")
  List<GpRuleJpaEntity> findByCampaignIdOrderByF1ScoreDesc(@Param("campaignId") String campaignId);
}
