package com.autolift.customer.api.command;

import com.autolift.customer.application.command.CreateCustomerCommand;
import com.autolift.customer.application.command.CreateCustomerCommandHandler;
import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.application.command.SuspendCustomerCommand;
import com.autolift.customer.application.command.SuspendCustomerCommandHandler;
import java.net.URI;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@Import({CreateCustomerCommandHandler.class, SuspendCustomerCommandHandler.class})
public class CustomerCommandController {

  private final CreateCustomerCommandHandler createHandler;
  private final SuspendCustomerCommandHandler suspendHandler;

  public CustomerCommandController(
      CreateCustomerCommandHandler createHandler, SuspendCustomerCommandHandler suspendHandler) {
    this.createHandler = createHandler;
    this.suspendHandler = suspendHandler;
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
}
