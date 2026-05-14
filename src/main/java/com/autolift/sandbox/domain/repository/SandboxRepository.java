package com.autolift.sandbox.domain.repository;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SandboxRepository extends JpaRepository<Sandbox, SandboxId> {
}