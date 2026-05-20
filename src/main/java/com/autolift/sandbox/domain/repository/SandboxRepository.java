package com.autolift.sandbox.domain.repository;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SandboxRepository {

  Sandbox save(Sandbox sandbox);

  Optional<Sandbox> findById(SandboxId id);

  List<Sandbox> findAll();

  Page<Sandbox> findAll(Pageable pageable);

  void deleteById(SandboxId id);
}
