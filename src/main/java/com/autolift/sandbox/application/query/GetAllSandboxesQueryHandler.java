package com.autolift.sandbox.application.query;

import com.autolift.sandbox.api.query.SandboxResponse;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GetAllSandboxesQueryHandler {
  private final SandboxRepository repository;

  public GetAllSandboxesQueryHandler(SandboxRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<SandboxResponse> handle(GetAllSandboxesQuery query) {
    return repository.findAll().stream()
        .map(s -> new SandboxResponse(s.getId().getId().toString(), s.getName()))
        .toList();
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public Page<SandboxResponse> handle(GetAllSandboxesQuery query, Pageable pageable) {
    return repository
        .findAll(pageable)
        .map(s -> new SandboxResponse(s.getId().getId().toString(), s.getName()));
  }
}
