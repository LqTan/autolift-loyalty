package com.autolift.ml.infrastructure.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MlJobProcessorTest {

  @Mock private ScheduledTaskLogRepository taskLogRepository;

  @Test
  void shouldPollPendingJobs() {
    when(taskLogRepository.save(any(ScheduledTaskLog.class))).thenAnswer(i -> i.getArgument(0));

    MlJobProcessor mlJobProcessor = new MlJobProcessor(null, taskLogRepository, null);
    mlJobProcessor.pollPendingJobs();

    verify(taskLogRepository, atLeast(1)).save(any(ScheduledTaskLog.class));
  }
}