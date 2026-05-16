package com.autolift.promotion.infrastructure.persistence.repository;

import com.autolift.promotion.infrastructure.persistence.entity.PromotionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionJpaRepository extends JpaRepository<PromotionJpaEntity, UUID> {}
