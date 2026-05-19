package com.autolift.targeting.infrastructure.persistence.repository;

import com.autolift.targeting.domain.model.CustomerFeatureSnapshot;
import com.autolift.targeting.domain.repository.CustomerFeatureSnapshotRepository;
import com.autolift.targeting.infrastructure.persistence.mapper.CustomerFeatureSnapshotMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerFeatureSnapshotRepositoryAdapter implements CustomerFeatureSnapshotRepository {

  private final CustomerFeatureSnapshotJpaRepository jpaRepository;

  public CustomerFeatureSnapshotRepositoryAdapter(
      CustomerFeatureSnapshotJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public CustomerFeatureSnapshot save(CustomerFeatureSnapshot snapshot) {
    return CustomerFeatureSnapshotMapper.toDomain(
        jpaRepository.save(CustomerFeatureSnapshotMapper.toEntity(snapshot)));
  }

  @Override
  public void saveAll(List<CustomerFeatureSnapshot> snapshots) {
    jpaRepository.saveAll(
        snapshots.stream()
            .map(CustomerFeatureSnapshotMapper::toEntity)
            .collect(Collectors.toList()));
  }

  @Override
  public Optional<CustomerFeatureSnapshot> findById(UUID id) {
    return jpaRepository.findById(id).map(CustomerFeatureSnapshotMapper::toDomain);
  }

  @Override
  public List<CustomerFeatureSnapshot> findByCustomerId(String customerId) {
    return jpaRepository.findByCustomerId(customerId).stream()
        .map(CustomerFeatureSnapshotMapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<CustomerFeatureSnapshot> findByCustomerIdAndCampaignId(
      String customerId, String campaignId) {
    return Optional.ofNullable(jpaRepository.findByCustomerIdAndCampaignId(customerId, campaignId))
        .map(CustomerFeatureSnapshotMapper::toDomain);
  }
}
