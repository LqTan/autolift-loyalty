package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.events.GpRulesExtractionRequestedEvent;
import com.autolift.ml.events.UpliftScoringRequestedEvent;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MlJobEventListener {

  private static final Logger log = LoggerFactory.getLogger(MlJobEventListener.class);

  private static final String PYTHON_WORKER_SCRIPT =
      "python scripts/run_ml_worker.py --db-url %s";

  @Async
  @EventListener
  public void handleUpliftScoringRequested(UpliftScoringRequestedEvent event) {
    log.info(
        "Received UpliftScoringRequestedEvent: jobId={}, campaignId={}",
        event.getJobId(),
        event.getCampaignId());

    String dbUrl = getDatabaseUrl();
    if (dbUrl == null) {
      log.error("DATABASE_URL not configured, cannot spawn Python worker");
      return;
    }

    spawnPythonWorker(String.format(PYTHON_WORKER_SCRIPT, dbUrl));
  }

  @Async
  @EventListener
  public void handleGpRulesExtractionRequested(GpRulesExtractionRequestedEvent event) {
    log.info(
        "Received GpRulesExtractionRequestedEvent: jobId={}, campaignId={}",
        event.getJobId(),
        event.getCampaignId());

    String dbUrl = getDatabaseUrl();
    if (dbUrl == null) {
      log.error("DATABASE_URL not configured, cannot spawn Python worker");
      return;
    }

    spawnPythonWorker(String.format(PYTHON_WORKER_SCRIPT, dbUrl));
  }

  private void spawnPythonWorker(String command) {
    try {
      log.info("Spawning Python worker: {}", command);

      ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
      pb.directory(new java.io.File("ml"));
      pb.redirectErrorStream(true);

      Process process = pb.start();

      CompletableFuture.runAsync(() -> {
        try {
          boolean completed = process.waitFor(2, TimeUnit.HOURS);
          if (completed) {
            int exitCode = process.exitValue();
            log.info("Python worker exited with code: {}", exitCode);
          } else {
            log.warn("Python worker timed out after 2 hours, destroying process");
            process.destroyForcibly();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.warn("Python worker interrupted");
        }
      });

      log.info("Python worker spawned successfully");
    } catch (IOException e) {
      log.error("Failed to spawn Python worker: {}", e.getMessage());
    }
  }

  private String getDatabaseUrl() {
    String url = System.getenv("DATABASE_URL");
    if (url == null || url.isEmpty()) {
      url = System.getenv("SPRING_DATASOURCE_URL");
    }
    if (url == null || url.isEmpty()) {
      url = System.getenv("POSTGRES_URL");
    }
    return url;
  }
}