package com.autolift.sandbox.domain.repository;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import java.util.List;
import java.util.Optional;

public interface SandboxRepository {

  Sandbox save(Sandbox sandbox);

  Optional<Sandbox> findById(SandboxId id);

  List<Sandbox> findAll();

  void deleteById(SandboxId id);
}
