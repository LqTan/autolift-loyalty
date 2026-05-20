package com.autolift.promotion.api.query;

import com.autolift.promotion.application.query.GetAllPromotionsQueryHandler;
import com.autolift.promotion.application.query.GetPromotionQuery;
import com.autolift.promotion.application.query.GetPromotionQueryHandler;
import com.autolift.promotion.application.query.PromotionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionQueryController {

  private final GetPromotionQueryHandler getByIdHandler;
  private final GetAllPromotionsQueryHandler getAllHandler;

  public PromotionQueryController(
      GetPromotionQueryHandler getByIdHandler, GetAllPromotionsQueryHandler getAllHandler) {
    this.getByIdHandler = getByIdHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get promotion by ID")
  @ApiResponse(responseCode = "200", description = "Promotion found")
  @ApiResponse(responseCode = "404", description = "Promotion not found")
  public ResponseEntity<PromotionView> getPromotion(@PathVariable String id) {
    PromotionView view = getByIdHandler.handle(new GetPromotionQuery(id));
    return ResponseEntity.ok(view);
  }

  @Operation(
      summary = "Get all promotions (paginated)",
      description = "Returns a paginated list of promotions. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated promotions",
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
                        "name": "Summer Sale",
                        "description": "...",
                        "promotionType": "PERCENTAGE",
                        "value": 10.0,
                        "minOrderAmount": 100.0,
                        "applicableCustomerSegment": "VIP",
                        "status": "ACTIVE",
                        "startDate": "2026-01-01T00:00:00Z",
                        "endDate": "2026-12-31T00:00:00Z"
                      }
                    ],
                    "totalElements": 30,
                    "totalPages": 2,
                    "size": 20,
                    "number": 0
                  }
                  """)))
  @GetMapping
  public Page<PromotionView> getAllPromotions(
      @Parameter(description = "Page number (0-indexed)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page (max 100)", example = "20")
          @RequestParam(defaultValue = "20")
          int size,
      @Parameter(description = "Sort field", example = "startDate")
          @RequestParam(defaultValue = "startDate")
          String sortBy,
      @Parameter(description = "Sort direction", example = "DESC")
          @RequestParam(defaultValue = "DESC")
          String sortDir) {
    Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
    Pageable pageable = PageRequest.of(page, Math.min(size, 100), sort);
    return getAllHandler.handle(pageable);
  }
}
