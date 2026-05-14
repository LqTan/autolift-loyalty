package com.autolift.sandbox.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SandboxId {

    private UUID id;

    private SandboxId(UUID id) {
        this.id = id;
    }

    public static SandboxId of(UUID id) {
        return new SandboxId(id);
    }

    public static SandboxId of(String id) {
        return new SandboxId(UUID.fromString(id));
    }

    public static SandboxId random() {
        return new SandboxId(UUID.randomUUID());
    }
}