package com.autolift.ml.api.query;

import com.autolift.ml.api.command.MlJobResponse;
import com.autolift.ml.application.query.GetMlJobHandler;
import com.autolift.ml.application.query.GetMlJobQuery;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ml/jobs")
public class MlJobQueryController {

  private final GetMlJobHandler getMlJobHandler;

  public MlJobQueryController(GetMlJobHandler getMlJobHandler) {
    this.getMlJobHandler = getMlJobHandler;
  }

  @GetMapping("/{jobId}")
  public ResponseEntity<MlJobResponse> getJob(@PathVariable UUID jobId) {
    return getMlJobHandler
        .handleByJobId(new GetMlJobQuery(jobId))
        .map(MlJobResponse::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/campaign/{campaignId}")
  public ResponseEntity<List<MlJobResponse>> getJobsByCampaign(@PathVariable String campaignId) {
    GetMlJobQuery query = new GetMlJobQuery();
    query.setCampaignId(campaignId);
    List<MlJobResponse> jobs =
        getMlJobHandler.handleByCampaign(query).stream().map(MlJobResponse::from).toList();
    return ResponseEntity.ok(jobs);
  }
}
