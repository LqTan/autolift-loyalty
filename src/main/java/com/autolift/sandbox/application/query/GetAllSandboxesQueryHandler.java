package com.autolift.sandbox.application.query;

import com.autolift.sandbox.api.query.SandboxResponse;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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
}