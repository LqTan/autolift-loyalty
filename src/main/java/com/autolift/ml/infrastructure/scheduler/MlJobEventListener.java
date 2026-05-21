package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.events.GpRulesExtractionRequestedEvent;
import com.autolift.ml.events.UpliftScoringRequestedEvent;
import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MlJobEventListener {

  private static final Logger log = LoggerFactory.getLogger(MlJobEventListener.class);

  private static final String DEFAULT_UPLIFT_SCORES_PATH = "ml/artifacts/outputs/customer_uplift_scores.csv";
  private static final String DEFAULT_GP_RULES_PATH = "ml/artifacts/outputs/gp_rules.csv";

  private final MlJobRepository mlJobRepository;

  public MlJobEventListener(MlJobRepository mlJobRepository) {
    this.mlJobRepository = mlJobRepository;
  }

  @Async
  @EventListener
  public void handleUpliftScoringRequested(UpliftScoringRequestedEvent event) {
    log.info(
        "Received UpliftScoringRequestedEvent: jobId={}, campaignId={}",
        event.getJobId(),
        event.getCampaignId());

    try {
      MlJob job = mlJobRepository.findById(MlJobId.of(event.getJobId())).orElse(null);
      if (job == null) {
        log.error("Job not found: {}", event.getJobId());
        return;
      }

      MlJob runningJob = job.markRunning();
      mlJobRepository.save(runningJob);

      MlJob completedJob = runningJob.markCompleted(DEFAULT_UPLIFT_SCORES_PATH);
      mlJobRepository.save(completedJob);

      log.info("Uplift scoring job completed immediately with pre-generated CSV: {}", DEFAULT_UPLIFT_SCORES_PATH);

    } catch (Exception e) {
      log.error("Failed to process uplift scoring job: {}", e.getMessage());
    }
  }

  @Async
  @EventListener
  public void handleGpRulesExtractionRequested(GpRulesExtractionRequestedEvent event) {
    log.info(
        "Received GpRulesExtractionRequestedEvent: jobId={}, campaignId={}",
        event.getJobId(),
        event.getCampaignId());

    try {
      MlJob job = mlJobRepository.findById(MlJobId.of(event.getJobId())).orElse(null);
      if (job == null) {
        log.error("Job not found: {}", event.getJobId());
        return;
      }

      MlJob runningJob = job.markRunning();
      mlJobRepository.save(runningJob);

      MlJob completedJob = runningJob.markCompleted(DEFAULT_GP_RULES_PATH);
      mlJobRepository.save(completedJob);

      log.info("GP rules extraction job completed immediately with pre-generated CSV: {}", DEFAULT_GP_RULES_PATH);

    } catch (Exception e) {
      log.error("Failed to process GP rules extraction job: {}", e.getMessage());
    }
  }
}