package com.autolift.customer.api.command;

import com.autolift.customer.application.command.CreateCustomerCommand;
import com.autolift.customer.application.command.CreateCustomerCommandHandler;
import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.application.command.ImportCustomersCommand;
import com.autolift.customer.application.command.ImportCustomersCommandHandler;
import com.autolift.customer.application.command.ImportCustomersResult;
import com.autolift.customer.application.command.SeedCustomersCommand;
import com.autolift.customer.application.command.SeedCustomersCommandHandler;
import com.autolift.customer.application.command.SuspendCustomerCommand;
import com.autolift.customer.application.command.SuspendCustomerCommandHandler;
import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
  ImportCustomersCommandHandler.class,
  SeedCustomersCommandHandler.class
})
public class CustomerCommandController {

  private final CreateCustomerCommandHandler createHandler;
  private final SuspendCustomerCommandHandler suspendHandler;
  private final ImportCustomersCommandHandler importHandler;
  private final SeedCustomersCommandHandler seedHandler;
  private final MlJobRepository mlJobRepository;

  public CustomerCommandController(
      CreateCustomerCommandHandler createHandler,
      SuspendCustomerCommandHandler suspendHandler,
      ImportCustomersCommandHandler importHandler,
      SeedCustomersCommandHandler seedHandler,
      MlJobRepository mlJobRepository) {
    this.createHandler = createHandler;
    this.suspendHandler = suspendHandler;
    this.importHandler = importHandler;
    this.seedHandler = seedHandler;
    this.mlJobRepository = mlJobRepository;
  }

  @Operation(
      summary = "Create a new customer",
      description = "Creates a single customer with name, email, phone and segment")
  @ApiResponse(
      responseCode = "201",
      description = "Customer created",
      content =
          @Content(
              mediaType = "application/json",
              schema =
                  @Schema(
                      implementation = CreateCustomerResult.class,
                      example =
                          "{\"id\": \"550e8400-e29b-41d4-a716-446655440000\", "
                              + "\"name\": \"John Doe\", "
                              + "\"email\": \"john@example.com\", "
                              + "\"phone\": \"123456789\", "
                              + "\"segment\": \"NORMAL\", "
                              + "\"status\": \"ACTIVE\"}")))
  @PostMapping
  public ResponseEntity<CreateCustomerResult> create(@RequestBody CreateCustomerRequest request) {
    CreateCustomerCommand command =
        new CreateCustomerCommand(
            request.name(), request.email(), request.phone(), request.segment());
    CreateCustomerResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/customers/" + result.id())).body(result);
  }

  @Operation(
      summary = "Suspend a customer",
      description = "Changes customer status from ACTIVE to SUSPENDED")
  @ApiResponse(responseCode = "204", description = "Customer suspended successfully")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  @PostMapping("/{customerId}/suspend")
  public ResponseEntity<Void> suspend(@PathVariable String customerId) {
    suspendHandler.handle(new SuspendCustomerCommand(customerId));
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Import customers from CSV file",
      description =
          "Bulk import customers from a CSV file. "
              + "Expected CSV format: id,customer_id,name,email,phone,segment,status,created_at,updated_at")
  @ApiResponse(
      responseCode = "200",
      description = "Import result",
      content =
          @Content(
              mediaType = "application/json",
              schema =
                  @Schema(
                      implementation = ImportCustomersResult.class,
                      example = "{\"imported\": 400162, \"failed\": 0}")))
  @PostMapping("/import")
  public ResponseEntity<ImportCustomersResult> importCustomers(
      @RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("customers_import_", ".csv");
    Files.copy(file.getInputStream(), tempFile);
    ImportCustomersResult result =
        importHandler.handle(new ImportCustomersCommand(tempFile.toString()));
    Files.deleteIfExists(tempFile);
    return ResponseEntity.ok(result);
  }

  @Operation(
      summary = "Seed X5 customers",
      description =
          "Creates a CUSTOMER_SEED ML job and asynchronously imports customers from ml/data/clients.csv.gz (X5 RetailHero dataset). "
              + "Processes in batches of 500 to avoid memory issues. "
              + "Returns job ID to track progress via GET /api/ml/jobs/{jobId}")
  @ApiResponse(
      responseCode = "202",
      description = "Accepted - seed job created, track via GET /api/ml/jobs/{jobId}",
      content =
          @Content(
              mediaType = "application/json",
              schema =
                  @Schema(
                      implementation = SeedCustomersResult.class,
                      example = "{\"jobId\": \"550e8400-e29b-41d4-a716-446655440000\", \"status\": \"PENDING\"}")))
  @PostMapping("/seed")
  public ResponseEntity<SeedCustomersResult> seedCustomers() {
    MlJob job = mlJobRepository.save(MlJob.createCustomerSeedJob());
    seedHandler.handle(new SeedCustomersCommand(job.getId().getId()));
    return ResponseEntity.accepted().body(new SeedCustomersResult(job.getId().getId(), "PENDING"));
  }
}
