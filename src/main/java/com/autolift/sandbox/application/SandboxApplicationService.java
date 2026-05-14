package com.autolift.sandbox.application;

import com.autolift.sandbox.domain.exception.SandboxNotFoundException;
import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.repository.SandboxRepository;
import com.autolift.sandbox.events.SandboxCreatedEvent;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SandboxApplicationService {

	private final SandboxRepository repository;

	public SandboxApplicationService(SandboxRepository repository) {
		this.repository = repository;
	}

	@Transactional
	public Sandbox create(String name) {
		Sandbox sandbox = new Sandbox(name);
		return repository.save(sandbox);
	}

	@Transactional
	public void delete(SandboxId id) {
		repository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<Sandbox> findAll() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public Sandbox findById(SandboxId id) {
		return repository.findById(id)
				.orElseThrow(() -> new SandboxNotFoundException("Sandbox not found with id: " + id));
	}

	@ApplicationModuleListener
	void on(SandboxCreatedEvent event) {
	}
}