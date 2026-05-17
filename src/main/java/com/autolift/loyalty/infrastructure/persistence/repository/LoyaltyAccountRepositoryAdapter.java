package com.autolift.loyalty.infrastructure.persistence.repository;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.infrastructure.persistence.mapper.LoyaltyAccountPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class LoyaltyAccountRepositoryAdapter implements LoyaltyAccountRepository {

  private final LoyaltyAccountJpaRepository jpaRepository;
  private final LoyaltyAccountPersistenceMapper mapper;

  public LoyaltyAccountRepositoryAdapter(
      LoyaltyAccountJpaRepository jpaRepository, LoyaltyAccountPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public LoyaltyAccount save(LoyaltyAccount account) {
    var entity = mapper.toEntity(account);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<LoyaltyAccount> findById(LoyaltyAccountId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public Optional<LoyaltyAccount> findByCustomerId(String customerId) {
    return jpaRepository.findAll().stream()
        .filter(e -> e.getCustomerId().equals(customerId))
        .map(mapper::toDomain)
        .findFirst();
  }

  @Override
  public List<LoyaltyAccount> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }
}