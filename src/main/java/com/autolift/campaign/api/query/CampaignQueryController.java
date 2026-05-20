package com.autolift.campaign.api.query;

import com.autolift.campaign.application.query.CampaignView;
import com.autolift.campaign.application.query.GetAllCampaignsQuery;
import com.autolift.campaign.application.query.GetAllCampaignsQueryHandler;
import com.autolift.campaign.application.query.GetCampaignQuery;
import com.autolift.campaign.application.query.GetCampaignQueryHandler;
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
@RequestMapping("/api/campaigns")
@Import({GetCampaignQueryHandler.class, GetAllCampaignsQueryHandler.class})
public class CampaignQueryController {

  private final GetCampaignQueryHandler getHandler;
  private final GetAllCampaignsQueryHandler getAllHandler;

  public CampaignQueryController(
      GetCampaignQueryHandler getHandler, GetAllCampaignsQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @Operation(
      summary = "Get all campaigns (paginated)",
      description = "Returns a paginated list of campaigns. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated campaigns",
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
                        "status": "ACTIVE",
                        "startDate": "2026-01-01T00:00:00Z",
                        "endDate": "2026-12-31T00:00:00Z",
                        "budgetAmount": 10000.00,
                        "budgetCurrency": "VND"
                      }
                    ],
                    "totalElements": 50,
                    "totalPages": 3,
                    "size": 20,
                    "number": 0
                  }
                  """)))
  @GetMapping
  public Page<CampaignResponse> findAll(
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
    return getAllHandler.handle(new GetAllCampaignsQuery(), pageable).map(this::toResponse);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get campaign by ID")
  @ApiResponse(responseCode = "200", description = "Campaign found")
  @ApiResponse(responseCode = "404", description = "Campaign not found")
  public ResponseEntity<CampaignResponse> findById(@PathVariable String id) {
    return getHandler
        .handle(new GetCampaignQuery(id))
        .map(c -> ResponseEntity.ok(toResponse(c)))
        .orElse(ResponseEntity.notFound().build());
  }

  private CampaignResponse toResponse(CampaignView view) {
    return new CampaignResponse(
        view.id(),
        view.name(),
        view.description(),
        view.status(),
        view.startDate(),
        view.endDate(),
        view.budgetAmount(),
        view.budgetCurrency());
  }
}
