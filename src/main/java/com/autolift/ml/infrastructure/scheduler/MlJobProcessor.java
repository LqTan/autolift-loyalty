package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
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
public class MlJobProcessor {

  private static final Logger log = LoggerFactory.getLogger(MlJobProcessor.class);
  private static final int MAX_RETRIES = 3;
  private static final String TASK_NAME = "ml_job_processor";

  private final MlJobRepository mlJobRepository;
  private final ScheduledTaskLogRepository taskLogRepository;
  private final ApplicationEventPublisher eventPublisher;

  public MlJobProcessor(
      MlJobRepository mlJobRepository,
      ScheduledTaskLogRepository taskLogRepository,
      ApplicationEventPublisher eventPublisher) {
    this.mlJobRepository = mlJobRepository;
    this.taskLogRepository = taskLogRepository;
    this.eventPublisher = eventPublisher;
  }

  @Scheduled(cron = "0/30 * * * * ?")
  @Transactional
  public void pollPendingJobs() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start(TASK_NAME);
    taskLogRepository.save(taskLog);
    try {
      pollJobsByType(MlJobType.UPLIFT_SCORING);
      pollJobsByType(MlJobType.GP_RULE_EXTRACTION);
      taskLogRepository.save(taskLog.markCompleted());
    } catch (Exception e) {
      log.error("ML job polling failed: {}", e.getMessage());
      taskLogRepository.save(taskLog.markFailed(e.getMessage()));
    }
  }

  private void pollJobsByType(MlJobType jobType) {
    Optional<MlJob> pendingJob =
        mlJobRepository.findFirstPendingByJobTypeOrderByCreatedAtAsc(jobType);
    if (pendingJob.isPresent()) {
      processJobWithRetry(pendingJob.get());
    }
  }

  private void processJobWithRetry(MlJob job) {
    int attempts = 0;
    Exception lastException = null;
    while (attempts < MAX_RETRIES) {
      try {
        processJob(job);
        return;
      } catch (Exception e) {
        attempts++;
        lastException = e;
        log.warn("ML job processing attempt {} failed: {}", attempts, e.getMessage());
        if (attempts < MAX_RETRIES) {
          try {
            Thread.sleep(5000L * attempts);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    }
    log.error("ML job failed after {} attempts: id={}", MAX_RETRIES, job.getId().getId());
    MlJob failedJob =
        job.markFailed("Failed after " + MAX_RETRIES + " attempts: " + lastException.getMessage());
    mlJobRepository.save(failedJob);
    eventPublisher.publishEvent(
        new MlJobFailedEvent(
            failedJob.getId().getId(),
            failedJob.getJobType(),
            failedJob.getCampaignId(),
            lastException.getMessage(),
            failedJob.getCompletedAt()));
  }

  void processJob(MlJob job) {
    log.info("Processing ML job: id={}, type={}", job.getId().getId(), job.getJobType());
    MlJob runningJob = job.markRunning();
    mlJobRepository.save(runningJob);
    try {
      executeJob(job);
      MlJob completedJob =
          runningJob.markCompleted("/tmp/ml_jobs/" + job.getId().getId() + "/result.csv");
      mlJobRepository.save(completedJob);
      eventPublisher.publishEvent(
          new MlJobCompletedEvent(
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
      eventPublisher.publishEvent(
          new MlJobFailedEvent(
              failedJob.getId().getId(),
              failedJob.getJobType(),
              failedJob.getCampaignId(),
              e.getMessage(),
              failedJob.getCompletedAt()));
      throw e;
    }
  }

  private void executeJob(MlJob job) {
    if (job.getJobType() == MlJobType.UPLIFT_SCORING) {
      log.info(
          "Would execute uplift scoring job via Python worker: campaignId={}, modelVersion={}",
          job.getCampaignId(),
          job.getModelVersion());
    } else if (job.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      log.info(
          "Would execute GP rule extraction job via Python worker: campaignId={}, modelVersion={}",
          job.getCampaignId(),
          job.getModelVersion());
    }
  }
}
