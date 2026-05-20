package com.autolift.customer.api.query;

import com.autolift.customer.application.query.GetAllCustomersQuery;
import com.autolift.customer.application.query.GetAllCustomersQueryHandler;
import com.autolift.customer.application.query.GetCustomerQuery;
import com.autolift.customer.application.query.GetCustomerQueryHandler;
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
@RequestMapping("/api/customers")
@Import({GetCustomerQueryHandler.class, GetAllCustomersQueryHandler.class})
public class CustomerQueryController {

  private final GetCustomerQueryHandler getHandler;
  private final GetAllCustomersQueryHandler getAllHandler;

  public CustomerQueryController(
      GetCustomerQueryHandler getHandler, GetAllCustomersQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @Operation(
      summary = "Get all customers (paginated)",
      description = "Returns a paginated list of customers. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated customers",
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
                        "id": "557b5575-7920-5bfe-b338-986baf22a9ba",
                        "name": "X5 Customer 00001276",
                        "email": "000012768d@x5.client",
                        "phone": "",
                        "segment": "NORMAL",
                        "status": "ACTIVE"
                      }
                    ],
                    "pageable": {
                      "pageNumber": 0,
                      "pageSize": 20,
                      "sort": {"direction": "DESC", "property": "createdAt"}
                    },
                    "totalElements": 400162,
                    "totalPages": 20009,
                    "last": false,
                    "first": true,
                    "size": 20,
                    "number": 0,
                    "empty": false
                  }
                  """)))
  @GetMapping
  public Page<CustomerResponse> findAll(
      @Parameter(description = "Page number (0-indexed)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page (max 100)", example = "20")
          @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Field to sort by", example = "createdAt")
          @RequestParam(defaultValue = "createdAt")
          String sortBy,
      @Parameter(description = "Sort direction", example = "DESC")
          @RequestParam(defaultValue = "DESC")
          String sortDir) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
    Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return getAllHandler.handle(new GetAllCustomersQuery(), pageable);
  }

  @GetMapping("/{customerId}")
  @Operation(
      summary = "Get customer by ID",
      description = "Returns a single customer by their UUID")
  @ApiResponse(responseCode = "200", description = "Customer found")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  public ResponseEntity<CustomerResponse> findById(@PathVariable String customerId) {
    try {
      return ResponseEntity.ok(getHandler.handle(new GetCustomerQuery(customerId)));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
