package com.autolift.targeting.infrastructure.persistence.repository;

import com.autolift.targeting.infrastructure.persistence.entity.CustomerUpliftScoreJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerUpliftScoreJpaRepository
    extends JpaRepository<CustomerUpliftScoreJpaEntity, UUID> {

  List<CustomerUpliftScoreJpaEntity> findByCampaignId(String campaignId);

  List<CustomerUpliftScoreJpaEntity> findByCustomerId(String customerId);

  @Query(
      "SELECT e FROM CustomerUpliftScoreJpaEntity e WHERE e.campaignId = :campaignId ORDER BY e.upliftScore DESC LIMIT :limit")
  List<CustomerUpliftScoreJpaEntity> findTopByCampaignIdOrderByUpliftScoreDesc(
      @Param("campaignId") String campaignId, @Param("limit") int limit);

  CustomerUpliftScoreJpaEntity findByCustomerIdAndCampaignId(String customerId, String campaignId);
}
