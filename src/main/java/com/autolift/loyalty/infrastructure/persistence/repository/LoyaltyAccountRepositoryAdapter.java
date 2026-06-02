package com.autolift.loyalty.infrastructure.persistence.repository;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.infrastructure.persistence.mapper.LoyaltyAccountPersistenceMapper;
import com.autolift.loyalty.infrastructure.persistence.mapper.PointTransactionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class LoyaltyAccountRepositoryAdapter implements LoyaltyAccountRepository {

  private final LoyaltyAccountJpaRepository jpaRepository;
  private final LoyaltyAccountPersistenceMapper mapper;
  private final PointTransactionJpaRepository transactionJpaRepository;
  private final PointTransactionPersistenceMapper transactionMapper;

  public LoyaltyAccountRepositoryAdapter(
      LoyaltyAccountJpaRepository jpaRepository,
      LoyaltyAccountPersistenceMapper mapper,
      PointTransactionJpaRepository transactionJpaRepository,
      PointTransactionPersistenceMapper transactionMapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.transactionJpaRepository = transactionJpaRepository;
    this.transactionMapper = transactionMapper;
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

  @Override
  public void saveTransaction(PointTransaction transaction) {
    var entity = transactionMapper.toEntity(transaction);
    transactionJpaRepository.save(entity);
  }
}
