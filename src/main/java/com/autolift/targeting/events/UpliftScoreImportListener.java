package com.autolift.targeting.events;

import com.autolift.ml.events.MlJobCompletedEvent;
import com.autolift.targeting.application.command.ImportUpliftScoresFromFileCommand;
import com.autolift.targeting.application.command.ImportUpliftScoresHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UpliftScoreImportListener {

  private static final Logger log = LoggerFactory.getLogger(UpliftScoreImportListener.class);

  private final ImportUpliftScoresHandler importUpliftScoresHandler;

  public UpliftScoreImportListener(ImportUpliftScoresHandler importUpliftScoresHandler) {
    this.importUpliftScoresHandler = importUpliftScoresHandler;
  }

  @EventListener
  public void onMlJobCompleted(MlJobCompletedEvent event) {
    log.info(
        ">>> UpliftScoreImportListener received MlJobCompletedEvent: jobId={}, campaignId={}, resultPath={}",
        event.getJobId(),
        event.getCampaignId(),
        event.getResultPath());

    if (event.getResultPath() == null || event.getResultPath().isBlank()) {
      log.warn("MlJobCompletedEvent has no resultPath, skipping import");
      return;
    }

    try {
      var command =
          new ImportUpliftScoresFromFileCommand(event.getResultPath(), event.getCampaignId());
      int count = importUpliftScoresHandler.handle(command);
      log.info(">>> Imported {} uplift scores for campaign {}", count, event.getCampaignId());
    } catch (Exception e) {
      log.error(
          ">>> Failed to import uplift scores for campaign {}: {}",
          event.getCampaignId(),
          e.getMessage(),
          e);
    }
  }
}
