package com.autolift.sandbox.application.command;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.events.SandboxCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CreateSandboxCommandHandler {
  private final SandboxRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public CreateSandboxCommandHandler(
      SandboxRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public SandboxCreatedResult handle(CreateSandboxCommand command) {
    Sandbox sandbox = new Sandbox(command.name());
    repository.save(sandbox);
    assert sandbox.getId() != null;
    eventPublisher.publishEvent(
        new SandboxCreatedEvent(sandbox.getId().getId().toString(), sandbox.getName()));
    return new SandboxCreatedResult(sandbox.getId().getId().toString(), sandbox.getName());
  }
}
