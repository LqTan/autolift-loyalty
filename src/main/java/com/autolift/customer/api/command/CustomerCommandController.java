package com.autolift.customer.api.command;

import com.autolift.customer.application.command.CreateCustomerCommand;
import com.autolift.customer.application.command.CreateCustomerCommandHandler;
import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.application.command.ImportCustomersCommand;
import com.autolift.customer.application.command.ImportCustomersCommandHandler;
import com.autolift.customer.application.command.ImportCustomersResult;
import com.autolift.customer.application.command.SuspendCustomerCommand;
import com.autolift.customer.application.command.SuspendCustomerCommandHandler;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/customers")
@Import({
  CreateCustomerCommandHandler.class,
  SuspendCustomerCommandHandler.class,
  ImportCustomersCommandHandler.class
})
public class CustomerCommandController {

  private final CreateCustomerCommandHandler createHandler;
  private final SuspendCustomerCommandHandler suspendHandler;
  private final ImportCustomersCommandHandler importHandler;

  public CustomerCommandController(
      CreateCustomerCommandHandler createHandler,
      SuspendCustomerCommandHandler suspendHandler,
      ImportCustomersCommandHandler importHandler) {
    this.createHandler = createHandler;
    this.suspendHandler = suspendHandler;
    this.importHandler = importHandler;
  }

  @PostMapping
  public ResponseEntity<CreateCustomerResult> create(@RequestBody CreateCustomerRequest request) {
    CreateCustomerCommand command =
        new CreateCustomerCommand(
            request.name(), request.email(), request.phone(), request.segment());
    CreateCustomerResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/customers/" + result.id())).body(result);
  }

  @PostMapping("/{customerId}/suspend")
  public ResponseEntity<Void> suspend(@PathVariable String customerId) {
    suspendHandler.handle(new SuspendCustomerCommand(customerId));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/import")
  public ResponseEntity<ImportCustomersResult> importCustomers(
      @RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    java.nio.file.Path tempFile =
        java.nio.file.Files.createTempFile("customers_import_", ".csv");
    Files.copy(file.getInputStream(), tempFile);
    ImportCustomersResult result =
        importHandler.handle(new ImportCustomersCommand(tempFile.toString()));
    Files.deleteIfExists(tempFile);
    return ResponseEntity.ok(result);
  }
}
