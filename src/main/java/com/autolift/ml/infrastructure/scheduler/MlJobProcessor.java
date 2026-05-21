package com.autolift.ml.infrastructure.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MlJobProcessor {

  private static final Logger log = LoggerFactory.getLogger(MlJobProcessor.class);

  public MlJobProcessor() {
  }

  public void pollPendingJobs() {
    log.info("MlJobProcessor.pollPendingJobs() called but ML jobs are handled by Python worker (ml/scripts/run_ml_worker.py). Skipping Java-based polling.");
  }
}