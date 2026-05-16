package com.autolift.sandbox.application.query;

import com.autolift.sandbox.api.query.SandboxResponse;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GetSandboxQueryHandler {
  private final SandboxRepository repository;

  public GetSandboxQueryHandler(SandboxRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public Optional<SandboxResponse> handle(GetSandboxQuery query) {
    return repository
        .findById(SandboxId.of(query.id()))
        .map(s -> new SandboxResponse(s.getId().getId().toString(), s.getName()));
  }
}
