package com.autolift.ml.api.command;

import com.autolift.ml.application.command.CreateMlJobCommand;
import com.autolift.ml.application.command.CreateMlJobHandler;
import com.autolift.ml.domain.model.MlJob;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ml/jobs")
public class MlJobCommandController {

  private final CreateMlJobHandler createMlJobHandler;

  public MlJobCommandController(CreateMlJobHandler createMlJobHandler) {
    this.createMlJobHandler = createMlJobHandler;
  }

  @PostMapping
  public ResponseEntity<MlJobResponse> createJob(@RequestBody CreateMlJobRequest request) {
    CreateMlJobCommand command = new CreateMlJobCommand(
        request.getJobType(),
        request.getCampaignId(),
        request.getModelVersion(),
        request.getInputParams(),
        request.getUpliftScoreJobId() != null ? java.util.UUID.fromString(request.getUpliftScoreJobId()) : null);
    MlJob job = createMlJobHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(MlJobResponse.from(job));
  }
}