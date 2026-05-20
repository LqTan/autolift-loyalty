package com.autolift.voucher.api.query;

import com.autolift.voucher.application.query.GetAllVouchersQuery;
import com.autolift.voucher.application.query.GetAllVouchersQueryHandler;
import com.autolift.voucher.application.query.GetVoucherQuery;
import com.autolift.voucher.application.query.GetVoucherQueryHandler;
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
@RequestMapping("/api/vouchers")
@Import({GetVoucherQueryHandler.class, GetAllVouchersQueryHandler.class})
public class VoucherQueryController {

  private final GetVoucherQueryHandler getHandler;
  private final GetAllVouchersQueryHandler getAllHandler;

  public VoucherQueryController(
      GetVoucherQueryHandler getHandler, GetAllVouchersQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @Operation(
      summary = "Get all vouchers (paginated)",
      description = "Returns a paginated list of vouchers. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated vouchers",
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
                        "code": "SUMMER2026",
                        "campaignId": "...",
                        "type": "PERCENTAGE",
                        "value": 10.0,
                        "minOrderAmount": 100.0,
                        "maxUsage": 1000,
                        "usedCount": 50,
                        "status": "ACTIVE",
                        "validFrom": "2026-01-01T00:00:00Z",
                        "validUntil": "2026-12-31T00:00:00Z"
                      }
                    ],
                    "totalElements": 100,
                    "totalPages": 5,
                    "size": 20,
                    "number": 0
                  }
                  """)))
  @GetMapping
  public Page<VoucherResponse> findAll(
      @Parameter(description = "Page number (0-indexed)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page (max 100)", example = "20")
          @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Sort field", example = "validFrom")
          @RequestParam(defaultValue = "validFrom")
          String sortBy,
      @Parameter(description = "Sort direction", example = "DESC")
          @RequestParam(defaultValue = "DESC")
          String sortDir) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
    Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return getAllHandler.handle(new GetAllVouchersQuery(), pageable);
  }

  @GetMapping("/{code}")
  @Operation(summary = "Get voucher by code")
  @ApiResponse(responseCode = "200", description = "Voucher found")
  @ApiResponse(responseCode = "404", description = "Voucher not found")
  public ResponseEntity<VoucherResponse> findByCode(@PathVariable String code) {
    try {
      return ResponseEntity.ok(getHandler.handle(new GetVoucherQuery(code)));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
