package com.autolift.explainability.infrastructure.persistence.repository;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import com.autolift.explainability.infrastructure.persistence.mapper.GpRuleMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GpRuleRepositoryAdapter implements GpRuleRepository {

  private final GpRuleJpaRepository jpaRepository;

  public GpRuleRepositoryAdapter(GpRuleJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public GpRule save(GpRule gpRule) {
    var entity = GpRuleMapper.toEntity(gpRule);
    var saved = jpaRepository.save(entity);
    return GpRuleMapper.toDomain(saved);
  }

  @Override
  public List<GpRule> saveAll(List<GpRule> gpRules) {
    var entities = gpRules.stream().map(GpRuleMapper::toEntity).collect(Collectors.toList());
    var saved = jpaRepository.saveAll(entities);
    return saved.stream().map(GpRuleMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<GpRule> findByCampaignId(String campaignId) {
    return jpaRepository.findByCampaignId(campaignId).stream()
        .map(GpRuleMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<GpRule> findByCampaignIdOrderByF1ScoreDesc(String campaignId) {
    return jpaRepository.findByCampaignIdOrderByF1ScoreDesc(campaignId).stream()
        .map(GpRuleMapper::toDomain)
        .collect(Collectors.toList());
  }
}
