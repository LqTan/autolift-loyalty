package com.autolift.ml.api.query;

import com.autolift.ml.api.command.MlJobResponse;
import com.autolift.ml.application.query.GetMlJobHandler;
import com.autolift.ml.application.query.GetMlJobMetricsHandler;
import com.autolift.ml.application.query.GetMlJobMetricsQuery;
import com.autolift.ml.application.query.GetMlJobQuery;
import com.autolift.ml.application.query.MlJobMetricsView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.UUID;
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
@RequestMapping("/api/ml/jobs")
public class MlJobQueryController {

  private final GetMlJobHandler getMlJobHandler;
  private final GetMlJobMetricsHandler getMlJobMetricsHandler;

  public MlJobQueryController(
      GetMlJobHandler getMlJobHandler, GetMlJobMetricsHandler getMlJobMetricsHandler) {
    this.getMlJobHandler = getMlJobHandler;
    this.getMlJobMetricsHandler = getMlJobMetricsHandler;
  }

  @GetMapping("/{jobId}")
  @Operation(summary = "Get ML job by ID")
  @ApiResponse(responseCode = "200", description = "Job found")
  @ApiResponse(responseCode = "404", description = "Job not found")
  public ResponseEntity<MlJobResponse> getJob(@PathVariable UUID jobId) {
    return getMlJobHandler
        .handleByJobId(new GetMlJobQuery(jobId))
        .map(MlJobResponse::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/{jobId}/metrics")
  @Operation(summary = "Get ML job metrics for charts")
  @ApiResponse(responseCode = "200", description = "Metrics found")
  @ApiResponse(responseCode = "404", description = "Job not found")
  @ApiResponse(responseCode = "204", description = "No metrics available for this job")
  public ResponseEntity<MlJobMetricsView> getJobMetrics(@PathVariable UUID jobId) {
    MlJobMetricsView metrics = getMlJobMetricsHandler.handle(new GetMlJobMetricsQuery(jobId));
    if (metrics.getMetrics() == null || metrics.getMetrics().isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(metrics);
  }

  @GetMapping("/campaign/{campaignId}")
  @Operation(
      summary = "Get ML jobs by campaign (paginated)",
      description =
          "Returns paginated ML jobs for a campaign. Default page size is 20, max is 100.")
  @ApiResponse(
      responseCode = "200",
      description = "Paginated ML jobs",
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
                        "jobType": "UPLIFT_SCORING",
                        "campaignId": "...",
                        "status": "COMPLETED",
                        "modelVersion": "v1.0",
                        "createdAt": "2026-01-15T10:30:00Z"
                      }
                    ],
                    "totalElements": 5,
                    "totalPages": 1,
                    "size": 20,
                    "number": 0
                  }
                  """)))
  public Page<MlJobResponse> getJobsByCampaign(
      @PathVariable String campaignId,
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
    GetMlJobQuery query = new GetMlJobQuery();
    query.setCampaignId(campaignId);
    return getMlJobHandler.handleByCampaign(query, pageable).map(MlJobResponse::from);
  }
}
