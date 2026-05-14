package com.autolift.sandbox.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "sandboxes", schema = "sandbox")
public class SandboxJpaEntity {

    @Id
    private UUID id;

    @Column
    private String name;

    public SandboxJpaEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}