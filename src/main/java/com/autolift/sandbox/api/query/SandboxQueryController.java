package com.autolift.sandbox.api.query;

import com.autolift.sandbox.application.query.GetAllSandboxesQuery;
import com.autolift.sandbox.application.query.GetAllSandboxesQueryHandler;
import com.autolift.sandbox.application.query.GetSandboxQuery;
import com.autolift.sandbox.application.query.GetSandboxQueryHandler;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sandbox")
@Import({GetSandboxQueryHandler.class, GetAllSandboxesQueryHandler.class})
public class SandboxQueryController {

  private final GetSandboxQueryHandler getHandler;
  private final GetAllSandboxesQueryHandler getAllHandler;

  public SandboxQueryController(
      GetSandboxQueryHandler getHandler, GetAllSandboxesQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping
  public List<SandboxResponse> findAll() {
    return getAllHandler.handle(new GetAllSandboxesQuery());
  }

  @GetMapping("/{id}")
  public ResponseEntity<SandboxResponse> findById(@PathVariable String id) {
    return getHandler
        .handle(new GetSandboxQuery(id))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
