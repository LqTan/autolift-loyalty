package com.autolift.sandbox.api;

import com.autolift.sandbox.application.SandboxApplicationService;
import com.autolift.sandbox.domain.model.Sandbox;
import com.autolift.sandbox.domain.valueobject.SandboxId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sandbox")
public class SandboxController {

	private final SandboxApplicationService service;

	public SandboxController(SandboxApplicationService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Sandbox> create(@RequestBody CreateSandboxRequest request) {
		Sandbox sandbox = service.create(request.name());
		return ResponseEntity.created(URI.create("/api/sandbox/" + sandbox.getId().getId())).body(sandbox);
	}

	@GetMapping
	public List<Sandbox> findAll() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Sandbox> findById(@PathVariable String id) {
		return ResponseEntity.ok(service.findById(SandboxId.of(id)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		service.delete(SandboxId.of(id));
		return ResponseEntity.noContent().build();
	}

	public record CreateSandboxRequest(String name) {
	}
}