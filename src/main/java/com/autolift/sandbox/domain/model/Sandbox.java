package com.autolift.sandbox.domain.model;

import com.autolift.sandbox.domain.valueobject.SandboxId;
import lombok.Getter;

@Getter
public class Sandbox {

    private final SandboxId id;
    private final String name;

    protected Sandbox() {
        this.id = null;
        this.name = null;
    }

    public Sandbox(String name) {
        this.id = SandboxId.random();
        this.name = name;
    }

    private Sandbox(SandboxId id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Sandbox of(SandboxId id, String name) {
        return new Sandbox(id, name);
    }
}