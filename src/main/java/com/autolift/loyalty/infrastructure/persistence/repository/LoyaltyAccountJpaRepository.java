package com.autolift.loyalty.infrastructure.persistence.repository;

import com.autolift.loyalty.infrastructure.persistence.entity.LoyaltyAccountJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyAccountJpaRepository extends JpaRepository<LoyaltyAccountJpaEntity, UUID> {}
