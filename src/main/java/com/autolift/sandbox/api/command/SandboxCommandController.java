package com.autolift.sandbox.api.command;

import com.autolift.sandbox.application.command.CreateSandboxCommand;
import com.autolift.sandbox.application.command.CreateSandboxCommandHandler;
import com.autolift.sandbox.application.command.DeleteSandboxCommand;
import com.autolift.sandbox.application.command.DeleteSandboxCommandHandler;
import com.autolift.sandbox.application.command.SandboxCreatedResult;
import java.net.URI;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sandbox")
@Import({CreateSandboxCommandHandler.class, DeleteSandboxCommandHandler.class})
public class SandboxCommandController {

  private final CreateSandboxCommandHandler createHandler;
  private final DeleteSandboxCommandHandler deleteHandler;

  public SandboxCommandController(
      CreateSandboxCommandHandler createHandler, DeleteSandboxCommandHandler deleteHandler) {
    this.createHandler = createHandler;
    this.deleteHandler = deleteHandler;
  }

  @PostMapping
  public ResponseEntity<SandboxCreatedResult> create(@RequestBody CreateSandboxRequest request) {
    CreateSandboxCommand command = new CreateSandboxCommand(request.name());
    SandboxCreatedResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/sandbox/" + result.id())).body(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    DeleteSandboxCommand command = new DeleteSandboxCommand(id);
    deleteHandler.handle(command);
    return ResponseEntity.noContent().build();
  }
}
