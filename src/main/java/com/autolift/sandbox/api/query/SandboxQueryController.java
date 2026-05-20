package com.autolift.sandbox.api.query;

import com.autolift.sandbox.application.query.GetAllSandboxesQuery;
import com.autolift.sandbox.application.query.GetAllSandboxesQueryHandler;
import com.autolift.sandbox.application.query.GetSandboxQuery;
import com.autolift.sandbox.application.query.GetSandboxQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @Operation(
      summary = "Get all sandboxes (paginated)",
      description = "Returns a paginated list of sandboxes. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated sandboxes",
      content =
          @Content(
              mediaType = "application/json",
              schema =
                  @Schema(
                      example =
                          """
                  {
                    "content": [
                      {
                        "id": "...",
                        "name": "Test Sandbox"
                      }
                    ],
                    "totalElements": 10,
                    "totalPages": 1,
                    "size": 20,
                    "number": 0
                  }
                  """)))
  @GetMapping
  public Page<SandboxResponse> findAll(
      @Parameter(description = "Page number (0-indexed)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page (max 100)", example = "20")
          @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Sort field", example = "createdAt")
          @RequestParam(defaultValue = "createdAt")
          String sortBy,
      @Parameter(description = "Sort direction", example = "DESC")
          @RequestParam(defaultValue = "DESC")
          String sortDir) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
    Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return getAllHandler.handle(new GetAllSandboxesQuery(), pageable);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get sandbox by ID")
  @ApiResponse(responseCode = "200", description = "Sandbox found")
  @ApiResponse(responseCode = "404", description = "Sandbox not found")
  public ResponseEntity<SandboxResponse> findById(@PathVariable String id) {
    return getHandler
        .handle(new GetSandboxQuery(id))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
