package com.autolift.ml.application.command;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.targeting.infrastructure.importfile.UpliftScoreCsvImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateMlJobHandler {

  private static final Logger log = LoggerFactory.getLogger(CreateMlJobHandler.class);

  private static final String DEFAULT_UPLIFT_SCORES_PATH = "ml/artifacts/outputs/customer_uplift_scores.csv";
  private static final String DEFAULT_GP_RULES_PATH = "ml/artifacts/outputs/gp_rules.csv";

  private final MlJobRepository mlJobRepository;
  private final UpliftScoreCsvImporter upliftScoreCsvImporter;

  public CreateMlJobHandler(
      MlJobRepository mlJobRepository, UpliftScoreCsvImporter upliftScoreCsvImporter) {
    this.mlJobRepository = mlJobRepository;
    this.upliftScoreCsvImporter = upliftScoreCsvImporter;
  }

  @Transactional
  public MlJob handle(CreateMlJobCommand command) {
    log.info(
        "Creating ML job: type={}, campaignId={}", command.getJobType(), command.getCampaignId());

    MlJob job;
    if (command.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      job =
          MlJob.createGpRuleExtractionJob(
              command.getCampaignId(),
              command.getModelVersion(),
              command.getInputParams(),
              command.getUpliftScoreJobId());
    } else {
      job =
          MlJob.createUpliftScoringJob(
              command.getCampaignId(), command.getModelVersion(), command.getInputParams());
    }

    MlJob savedJob = mlJobRepository.save(job);
    log.info("Created ML job: id={}", savedJob.getId().getId());

    MlJob completedJob;
    if (savedJob.getJobType() == MlJobType.UPLIFT_SCORING) {
      completedJob = savedJob.markCompleted(DEFAULT_UPLIFT_SCORES_PATH);
      log.info("Uplift scoring job marked as COMPLETED with result path: {}", DEFAULT_UPLIFT_SCORES_PATH);
    } else if (savedJob.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      completedJob = savedJob.markCompleted(DEFAULT_GP_RULES_PATH);
      log.info("GP rule extraction job marked as COMPLETED with result path: {}", DEFAULT_GP_RULES_PATH);
    } else {
      completedJob = savedJob.markCompleted(null);
    }

    MlJob finalJob = mlJobRepository.save(completedJob);

    if (finalJob.getJobType() == MlJobType.UPLIFT_SCORING) {
      importUpliftScores(finalJob.getCampaignId(), DEFAULT_UPLIFT_SCORES_PATH);
    }

    return finalJob;
  }

  private void importUpliftScores(String campaignId, String resultPath) {
    try {
      int count = upliftScoreCsvImporter.importFromFilePath(resultPath, campaignId);
      log.info("Auto-imported {} uplift scores for campaign: {}", count, campaignId);
    } catch (Exception e) {
      log.error("Failed to import uplift scores for campaign {}: {}", campaignId, e.getMessage());
    }
  }
}