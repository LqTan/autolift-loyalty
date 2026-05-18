package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.ml.events.MlJobCompletedEvent;
import com.autolift.ml.events.MlJobFailedEvent;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MlJobScheduler {

  private static final Logger log = LoggerFactory.getLogger(MlJobScheduler.class);

  private final MlJobRepository mlJobRepository;
  private final ApplicationEventPublisher eventPublisher;

  public MlJobScheduler(MlJobRepository mlJobRepository, ApplicationEventPublisher eventPublisher) {
    this.mlJobRepository = mlJobRepository;
    this.eventPublisher = eventPublisher;
  }

  @Scheduled(fixedDelay = 30000)
  @Transactional
  public void pollPendingJobs() {
    pollJobsByType(MlJobType.UPLIFT_SCORING);
    pollJobsByType(MlJobType.GP_RULE_EXTRACTION);
  }

  private void pollJobsByType(MlJobType jobType) {
    Optional<MlJob> pendingJob = mlJobRepository.findFirstPendingByJobTypeOrderByCreatedAtAsc(jobType);
    if (pendingJob.isPresent()) {
      processJob(pendingJob.get());
    }
  }

  private void processJob(MlJob job) {
    log.info("Processing ML job: id={}, type={}", job.getId().getId(), job.getJobType());
    MlJob runningJob = job.markRunning();
    mlJobRepository.save(runningJob);
    try {
      executeJob(job);
      MlJob completedJob = runningJob.markCompleted("/tmp/ml_jobs/" + job.getId().getId() + "/result.csv");
      mlJobRepository.save(completedJob);
      eventPublisher.publishEvent(new MlJobCompletedEvent(
          completedJob.getId().getId(),
          completedJob.getJobType(),
          completedJob.getCampaignId(),
          "/tmp/ml_jobs/" + job.getId().getId() + "/result.csv",
          completedJob.getCompletedAt()));
      log.info("ML job completed: id={}", job.getId().getId());
    } catch (Exception e) {
      log.error("ML job failed: id={}, error={}", job.getId().getId(), e.getMessage());
      MlJob failedJob = runningJob.markFailed(e.getMessage());
      mlJobRepository.save(failedJob);
      eventPublisher.publishEvent(new MlJobFailedEvent(
          failedJob.getId().getId(),
          failedJob.getJobType(),
          failedJob.getCampaignId(),
          e.getMessage(),
          failedJob.getCompletedAt()));
    }
  }

  private void executeJob(MlJob job) {
    if (job.getJobType() == MlJobType.UPLIFT_SCORING) {
      log.info("Would execute uplift scoring job via Python worker: campaignId={}, modelVersion={}",
          job.getCampaignId(), job.getModelVersion());
    } else if (job.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      log.info("Would execute GP rule extraction job via Python worker: campaignId={}, modelVersion={}",
          job.getCampaignId(), job.getModelVersion());
    }
  }
}