package com.autolift.sandbox.domain.model;

import com.autolift.sandbox.domain.valueobject.SandboxId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "sandboxes", schema = "sandbox")
public class Sandbox {

	@EmbeddedId
	private SandboxId id;

	@Column
	private String name;

	protected Sandbox() {
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