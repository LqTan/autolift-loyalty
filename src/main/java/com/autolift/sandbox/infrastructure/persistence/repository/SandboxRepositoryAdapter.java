package com.autolift.sandbox.infrastructure.persistence.repository;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import com.autolift.sandbox.infrastructure.persistence.mapper.SandboxPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class SandboxRepositoryAdapter implements SandboxRepository {

  private final SandboxJpaRepository jpaRepository;
  private final SandboxPersistenceMapper mapper;

  public SandboxRepositoryAdapter(
      SandboxJpaRepository jpaRepository, SandboxPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Sandbox save(Sandbox sandbox) {
    var entity = mapper.toEntity(sandbox);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Sandbox> findById(SandboxId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public List<Sandbox> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Page<Sandbox> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(mapper::toDomain);
  }

  @Override
  public void deleteById(SandboxId id) {
    jpaRepository.deleteById(id.getId());
  }
}
