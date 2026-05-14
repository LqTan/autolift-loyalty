package com.autolift.sandbox.application.command;

import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.events.DomainEventPublisher;
import com.autolift.sandbox.events.SandboxCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CreateSandboxCommandHandler {
    private final SandboxRepository repository;
    private final DomainEventPublisher eventPublisher;

    public CreateSandboxCommandHandler(SandboxRepository repository, DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @org.springframework.transaction.annotation.Transactional
    public SandboxCreatedResult handle(CreateSandboxCommand command) {
        Sandbox sandbox = new Sandbox(command.name());
        repository.save(sandbox);
        eventPublisher.publish(new SandboxCreatedEvent(
            sandbox.getId().getId().toString(), sandbox.getName()));
        return new SandboxCreatedResult(sandbox.getId().getId().toString(), sandbox.getName());
    }
}