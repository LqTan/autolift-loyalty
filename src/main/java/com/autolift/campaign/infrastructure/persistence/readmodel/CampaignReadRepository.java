package com.autolift.campaign.infrastructure.persistence.readmodel;

import com.autolift.campaign.application.query.CampaignView;
import com.autolift.campaign.infrastructure.persistence.entity.CampaignJpaEntity;
import com.autolift.campaign.infrastructure.persistence.repository.CampaignJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CampaignReadRepository {

  private final CampaignJpaRepository jpaRepository;

  public CampaignReadRepository(CampaignJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public List<CampaignView> findAll() {
    return jpaRepository.findAll().stream().map(this::toView).toList();
  }

  public Page<CampaignView> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(this::toView);
  }

  public Optional<CampaignView> findById(String id) {
    return jpaRepository.findById(UUID.fromString(id)).map(this::toView);
  }

  public List<CampaignView> findByStatus(String status) {
    return jpaRepository.findAll().stream()
        .filter(e -> e.getStatus().equals(status))
        .map(this::toView)
        .toList();
  }

  private CampaignView toView(CampaignJpaEntity entity) {
    return new CampaignView(
        entity.getId().toString(),
        entity.getName(),
        entity.getDescription(),
        entity.getStatus(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getBudgetAmount(),
        entity.getBudgetCurrency());
  }
}
