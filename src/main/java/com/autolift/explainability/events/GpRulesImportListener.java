package com.autolift.explainability.events;

import com.autolift.explainability.application.command.ImportGpRulesFromFileCommand;
import com.autolift.explainability.application.command.ImportGpRulesHandler;
import com.autolift.ml.domain.valueobject.MlJobType;
import com.autolift.ml.events.MlJobCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GpRulesImportListener {

  private static final Logger log = LoggerFactory.getLogger(GpRulesImportListener.class);

  private final ImportGpRulesHandler importGpRulesHandler;

  public GpRulesImportListener(ImportGpRulesHandler importGpRulesHandler) {
    this.importGpRulesHandler = importGpRulesHandler;
  }

  @EventListener
  public void onMlJobCompleted(MlJobCompletedEvent event) {
    if (event.getJobType() != MlJobType.GP_RULE_EXTRACTION) {
      return;
    }

    log.info(
        ">>> GpRulesImportListener received MlJobCompletedEvent: jobId={}, campaignId={}, resultPath={}",
        event.getJobId(),
        event.getCampaignId(),
        event.getResultPath());

    if (event.getResultPath() == null || event.getResultPath().isBlank()) {
      log.warn("MlJobCompletedEvent has no resultPath, skipping GP rules import");
      return;
    }

    try {
      var command =
          new ImportGpRulesFromFileCommand(
              event.getResultPath(), event.getCampaignId(), event.getJobType().name());
      int count = importGpRulesHandler.handle(command);
      log.info(">>> Imported {} GP rules for campaign {}", count, event.getCampaignId());
    } catch (Exception e) {
      log.error(
          ">>> Failed to import GP rules for campaign {}: {}",
          event.getCampaignId(),
          e.getMessage(),
          e);
    }
  }
}
