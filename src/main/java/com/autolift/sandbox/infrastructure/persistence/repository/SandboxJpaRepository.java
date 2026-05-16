package com.autolift.sandbox.infrastructure.persistence.repository;

import com.autolift.sandbox.infrastructure.persistence.entity.SandboxJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SandboxJpaRepository extends JpaRepository<SandboxJpaEntity, UUID> {}
