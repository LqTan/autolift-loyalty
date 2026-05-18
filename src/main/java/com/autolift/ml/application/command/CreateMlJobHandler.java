package com.autolift.ml.application.command;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateMlJobHandler {

  private static final Logger log = LoggerFactory.getLogger(CreateMlJobHandler.class);

  private final MlJobRepository mlJobRepository;
  private final ApplicationEventPublisher eventPublisher;

  public CreateMlJobHandler(MlJobRepository mlJobRepository, ApplicationEventPublisher eventPublisher) {
    this.mlJobRepository = mlJobRepository;
    this.eventPublisher = eventPublisher;
  }

  @Transactional
  public MlJob handle(CreateMlJobCommand command) {
    log.info("Creating ML job: type={}, campaignId={}", command.getJobType(), command.getCampaignId());

    MlJob job;
    if (command.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      job = MlJob.createGpRuleExtractionJob(
          command.getCampaignId(),
          command.getModelVersion(),
          command.getInputParams(),
          command.getUpliftScoreJobId());
    } else {
      job = MlJob.createUpliftScoringJob(
          command.getCampaignId(),
          command.getModelVersion(),
          command.getInputParams());
    }

    MlJob savedJob = mlJobRepository.save(job);
    log.info("Created ML job: id={}", savedJob.getId().getId());

    if (savedJob.getJobType() == MlJobType.UPLIFT_SCORING) {
      eventPublisher.publishEvent(new com.autolift.ml.events.UpliftScoringRequestedEvent(
          savedJob.getId().getId(),
          savedJob.getCampaignId(),
          savedJob.getModelVersion(),
          savedJob.getInputParams(),
          savedJob.getCreatedAt()));
    } else if (savedJob.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      eventPublisher.publishEvent(new com.autolift.ml.events.GpRulesExtractionRequestedEvent(
          savedJob.getId().getId(),
          savedJob.getCampaignId(),
          savedJob.getUpliftScoreJobId(),
          savedJob.getModelVersion(),
          savedJob.getInputParams(),
          savedJob.getCreatedAt()));
    }

    return savedJob;
  }
}