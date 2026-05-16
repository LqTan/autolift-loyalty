package com.autolift.sandbox.application.command;

import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import org.springframework.stereotype.Component;

@Component
public class DeleteSandboxCommandHandler {
  private final SandboxRepository repository;

  public DeleteSandboxCommandHandler(SandboxRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(DeleteSandboxCommand command) {
    repository.deleteById(SandboxId.of(command.id()));
  }
}
