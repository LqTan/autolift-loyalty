package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.events.MlJobCompletedEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MlJobCompletedPoller {

  private static final Logger log = LoggerFactory.getLogger(MlJobCompletedPoller.class);

  private final MlJobRepository mlJobRepository;
  private final ApplicationEventPublisher eventPublisher;

  private final Set<UUID> notifiedJobs = new HashSet<>();

  public MlJobCompletedPoller(
      MlJobRepository mlJobRepository, ApplicationEventPublisher eventPublisher) {
    this.mlJobRepository = mlJobRepository;
    this.eventPublisher = eventPublisher;
  }

  @Scheduled(fixedDelay = 5000)
  public void pollAndNotify() {
    var completedJobs = mlJobRepository.findByStatus(MlJobStatus.COMPLETED);

    for (MlJob job : completedJobs) {
      UUID jobId = job.getId().getId();
      if (!notifiedJobs.contains(jobId)) {
        log.info(
            ">>> Poller detected COMPLETED job: id={}, campaignId={}, resultPath={}",
            jobId,
            job.getCampaignId(),
            job.getResultPath());

        eventPublisher.publishEvent(
            new MlJobCompletedEvent(
                jobId,
                job.getJobType(),
                job.getCampaignId(),
                job.getResultPath(),
                job.getCompletedAt()));

        notifiedJobs.add(jobId);
        log.info(">>> Published MlJobCompletedEvent for job: {}", jobId);
      }
    }
  }
}
