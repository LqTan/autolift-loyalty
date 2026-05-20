package com.autolift.promotion.infrastructure.persistence.repository;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.infrastructure.persistence.entity.PromotionJpaEntity;
import com.autolift.promotion.infrastructure.persistence.mapper.PromotionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PromotionRepositoryAdapter implements PromotionRepository {

  private final PromotionJpaRepository jpaRepository;
  private final PromotionPersistenceMapper mapper;

  public PromotionRepositoryAdapter(
      PromotionJpaRepository jpaRepository, PromotionPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Promotion> findById(PromotionId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public List<Promotion> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Page<Promotion> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(mapper::toDomain);
  }

  @Override
  public Promotion save(Promotion promotion) {
    PromotionJpaEntity entity = mapper.toEntity(promotion);
    PromotionJpaEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  public void delete(Promotion promotion) {
    jpaRepository.delete(mapper.toEntity(promotion));
  }
}
