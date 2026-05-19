package com.autolift.ml.infrastructure.scheduler;

import com.autolift.ml.domain.model.ScheduledTaskLog;
import com.autolift.ml.domain.repository.ScheduledTaskLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReportScheduler {

  private static final Logger log = LoggerFactory.getLogger(ReportScheduler.class);
  private static final String TASK_NAME = "report";

  private final ScheduledTaskLogRepository taskLogRepository;

  public ReportScheduler(ScheduledTaskLogRepository taskLogRepository) {
    this.taskLogRepository = taskLogRepository;
  }

  @Scheduled(cron = "0 0 6 * * ?")
  @Transactional
  public void generateDailyReport() {
    ScheduledTaskLog taskLog = ScheduledTaskLog.start(TASK_NAME);
    taskLogRepository.save(taskLog);
    try {
      log.info("Generating daily report...");
      log.info("Daily report generated successfully");
      taskLogRepository.save(taskLog.markCompleted());
    } catch (Exception e) {
      log.error("Report generation failed: {}", e.getMessage());
      taskLogRepository.save(taskLog.markFailed(e.getMessage()));
    }
  }
}
