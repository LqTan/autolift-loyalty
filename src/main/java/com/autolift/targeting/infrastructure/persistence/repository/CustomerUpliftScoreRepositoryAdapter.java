package com.autolift.targeting.infrastructure.persistence.repository;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.repository.CustomerUpliftScoreRepository;
import com.autolift.targeting.domain.valueobject.UpliftScoreId;
import com.autolift.targeting.infrastructure.persistence.mapper.CustomerUpliftScoreMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerUpliftScoreRepositoryAdapter implements CustomerUpliftScoreRepository {

  private final CustomerUpliftScoreJpaRepository jpaRepository;

  public CustomerUpliftScoreRepositoryAdapter(CustomerUpliftScoreJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public CustomerUpliftScore save(CustomerUpliftScore score) {
    return CustomerUpliftScoreMapper.toDomain(jpaRepository.save(CustomerUpliftScoreMapper.toEntity(score)));
  }

  @Override
  public void saveAll(List<CustomerUpliftScore> scores) {
    jpaRepository.saveAll(scores.stream()
        .map(CustomerUpliftScoreMapper::toEntity)
        .collect(Collectors.toList()));
  }

  @Override
  public Optional<CustomerUpliftScore> findById(UpliftScoreId id) {
    return jpaRepository.findById(id.getId()).map(CustomerUpliftScoreMapper::toDomain);
  }

  @Override
  public List<CustomerUpliftScore> findByCampaignId(String campaignId) {
    return jpaRepository.findByCampaignId(campaignId).stream()
        .map(CustomerUpliftScoreMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<CustomerUpliftScore> findByCustomerId(String customerId) {
    return jpaRepository.findByCustomerId(customerId).stream()
        .map(CustomerUpliftScoreMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<CustomerUpliftScore> findTopByCampaignIdOrderByUpliftScoreDesc(String campaignId, int limit) {
    return jpaRepository.findTopByCampaignIdOrderByUpliftScoreDesc(campaignId, limit).stream()
        .map(CustomerUpliftScoreMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CustomerUpliftScore> findByCustomerIdAndCampaignId(String customerId, String campaignId) {
    return Optional.ofNullable(jpaRepository.findByCustomerIdAndCampaignId(customerId, campaignId))
        .map(CustomerUpliftScoreMapper::toDomain);
  }
}