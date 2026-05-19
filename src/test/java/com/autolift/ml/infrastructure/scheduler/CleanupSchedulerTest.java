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
class CleanupSchedulerTest {

  @Mock private ScheduledTaskLogRepository taskLogRepository;

  @Test
  void shouldSaveTaskLogOnCleanup() {
    when(taskLogRepository.save(any(ScheduledTaskLog.class))).thenAnswer(i -> i.getArgument(0));
    when(taskLogRepository.findByStatusAndStartedAtBefore(any(), any())).thenReturn(java.util.List.of());

    CleanupScheduler cleanupScheduler = new CleanupScheduler(null, taskLogRepository);
    cleanupScheduler.cleanup();

    verify(taskLogRepository, atLeast(1)).save(any(ScheduledTaskLog.class));
  }
}