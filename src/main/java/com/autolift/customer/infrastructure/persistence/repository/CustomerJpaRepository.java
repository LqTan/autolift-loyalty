package com.autolift.customer.infrastructure.persistence.repository;

import com.autolift.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {}
