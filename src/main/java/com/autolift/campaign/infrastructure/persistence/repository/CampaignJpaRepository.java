package com.autolift.campaign.infrastructure.persistence.repository;

import com.autolift.campaign.infrastructure.persistence.entity.CampaignJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CampaignJpaRepository extends JpaRepository<CampaignJpaEntity, UUID> {
}