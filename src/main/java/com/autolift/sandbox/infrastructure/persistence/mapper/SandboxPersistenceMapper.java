package com.autolift.sandbox.infrastructure.persistence.mapper;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import com.autolift.sandbox.infrastructure.persistence.entity.SandboxJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SandboxPersistenceMapper {

    public Sandbox toDomain(SandboxJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Sandbox.of(SandboxId.of(entity.getId()), entity.getName());
    }

    public SandboxJpaEntity toEntity(Sandbox domain) {
        if (domain == null) {
            return null;
        }
        return new SandboxJpaEntity(domain.getId().getId(), domain.getName());
    }
}