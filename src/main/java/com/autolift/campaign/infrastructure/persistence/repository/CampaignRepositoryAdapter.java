package com.autolift.campaign.infrastructure.persistence.repository;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.infrastructure.persistence.mapper.CampaignPersistenceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CampaignRepositoryAdapter implements CampaignRepository {

  private final CampaignJpaRepository jpaRepository;
  private final CampaignPersistenceMapper mapper;

  public CampaignRepositoryAdapter(
      CampaignJpaRepository jpaRepository, CampaignPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Campaign save(Campaign campaign) {
    var entity = mapper.toEntity(campaign);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public java.util.Optional<Campaign> findById(CampaignId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public java.util.List<Campaign> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public java.util.List<Campaign> findByStatus(
      com.autolift.campaign.domain.valueobject.CampaignStatus status) {
    return jpaRepository.findAll().stream()
        .filter(e -> e.getStatus().equals(status.name()))
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteById(CampaignId id) {
    jpaRepository.deleteById(id.getId());
  }
}
