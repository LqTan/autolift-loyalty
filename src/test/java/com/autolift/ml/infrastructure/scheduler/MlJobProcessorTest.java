package com.autolift.ml.infrastructure.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MlJobProcessorTest {

  @Test
  void shouldPollPendingJobs() {
    MlJobProcessor mlJobProcessor = new MlJobProcessor();
    mlJobProcessor.pollPendingJobs();
  }
}
