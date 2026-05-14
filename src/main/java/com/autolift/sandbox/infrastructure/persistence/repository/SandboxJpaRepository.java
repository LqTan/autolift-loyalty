package com.autolift.sandbox.infrastructure.persistence.repository;

import com.autolift.sandbox.infrastructure.persistence.entity.SandboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SandboxJpaRepository extends JpaRepository<SandboxJpaEntity, UUID> {
}