package com.autolift.ml.infrastructure.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.ml.events.MlJobCompletedEvent;
import com.autolift.ml.events.MlJobFailedEvent;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MlJobProcessorTest {

  @Mock private MlJobRepository mlJobRepository;
  @Mock private ScheduledTaskLogRepository taskLogRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  private MlJobProcessor mlJobProcessor;

  @BeforeEach
  void setUp() {
    mlJobProcessor = new MlJobProcessor(mlJobRepository, taskLogRepository, eventPublisher);
  }

  @Test
  void shouldPollPendingJobs() {
    when(taskLogRepository.save(any(ScheduledTaskLog.class))).thenAnswer(i -> i.getArgument(0));
    when(mlJobRepository.findFirstPendingByJobTypeOrderByCreatedAtAsc(any())).thenReturn(Optional.empty());

    mlJobProcessor.pollPendingJobs();

    verify(taskLogRepository, atLeast(1)).save(any(ScheduledTaskLog.class));
  }

  @Test
  void shouldPublishCompletionEventOnJobSuccess() {
    MlJob mlJob = mock(MlJob.class);
    MlJobId jobId = MlJobId.random();
    when(mlJob.getId()).thenReturn(jobId);
    when(mlJob.getJobType()).thenReturn(MlJobType.UPLIFT_SCORING);
    when(mlJob.getCampaignId()).thenReturn("campaign-1");
    when(mlJob.getModelVersion()).thenReturn("v1");
    when(mlJobRepository.save(any(MlJob.class))).thenAnswer(i -> i.getArgument(0));
    when(taskLogRepository.save(any(ScheduledTaskLog.class))).thenAnswer(i -> i.getArgument(0));

    mlJobProcessor.processJob(mlJob);

    verify(eventPublisher).publishEvent(any(MlJobCompletedEvent.class));
  }

  @Test
  void shouldPublishFailureEventOnJobFailure() {
    MlJob mlJob = mock(MlJob.class);
    MlJobId jobId = MlJobId.random();
    when(mlJob.getId()).thenReturn(jobId);
    when(mlJob.getJobType()).thenReturn(MlJobType.UPLIFT_SCORING);
    when(mlJob.getCampaignId()).thenReturn("campaign-1");
    when(mlJobRepository.save(any(MlJob.class))).thenThrow(new RuntimeException("Execution failed"));
    when(taskLogRepository.save(any(ScheduledTaskLog.class))).thenAnswer(i -> i.getArgument(0));

    assertThrows(RuntimeException.class, () -> mlJobProcessor.processJob(mlJob));

    verify(eventPublisher).publishEvent(any(MlJobFailedEvent.class));
  }
}