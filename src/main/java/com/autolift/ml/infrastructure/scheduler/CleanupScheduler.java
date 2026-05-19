package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
import com.autolift.ml.domain.repository.MlJobRepository;
import java.io.File;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CleanupScheduler {

  private static final Logger log = LoggerFactory.getLogger(CleanupScheduler.class);
  private static final String TASK_NAME = "cleanup";
  private static final int RETENTION_DAYS = 30;

  private final MlJobRepository mlJobRepository;
  private final ScheduledTaskLogRepository taskLogRepository;

  public CleanupScheduler(
      MlJobRepository mlJobRepository, ScheduledTaskLogRepository taskLogRepository) {
    this.mlJobRepository = mlJobRepository;
    this.taskLogRepository = taskLogRepository;
  }

  @Scheduled(cron = "0 0 2 * * ?")
  @Transactional
  public void cleanup() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start(TASK_NAME);
    taskLogRepository.save(taskLog);
    try {
      cleanupOldMlJobs();
      cleanupOldTaskLogs();
      cleanupTempFiles();
      taskLogRepository.save(taskLog.markCompleted());
      log.info("Cleanup completed successfully");
    } catch (Exception e) {
      log.error("Cleanup failed: {}", e.getMessage());
      taskLogRepository.save(taskLog.markFailed(e.getMessage()));
    }
  }

  private void cleanupOldMlJobs() {
    Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
    List<MlJob> oldJobs = mlJobRepository.findByCompletedAtBefore(cutoff);
    for (MlJob job : oldJobs) {
      mlJobRepository.save(job.markFailed("Cleaned up due to retention policy"));
    }
    log.info("Marked {} old ML jobs as cleaned up", oldJobs.size());
  }

  private void cleanupOldTaskLogs() {
    Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
    var oldLogs =
        taskLogRepository.findByStatusAndStartedAtBefore("COMPLETED", cutoff);
    for (ScheduledTaskLog logEntry : oldLogs) {
      taskLogRepository.deleteById(logEntry.getId().getId());
    }
    log.info("Deleted {} old task logs", oldLogs.size());
  }

  private void cleanupTempFiles() {
    File tempDir = new File("/tmp/ml_jobs");
    if (tempDir.exists() && tempDir.isDirectory()) {
      File[] subdirs = tempDir.listFiles();
      if (subdirs != null) {
        Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
        for (File subdir : subdirs) {
          long ageInDays =
              ChronoUnit.DAYS.between(Instant.ofEpochMilli(subdir.lastModified()), Instant.now());
          if (ageInDays > RETENTION_DAYS) {
            deleteDirectory(subdir);
            log.info("Deleted temp directory: {}", subdir.getName());
          }
        }
      }
    }
  }

  private void deleteDirectory(File dir) {
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    dir.delete();
  }
}