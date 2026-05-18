package com.autolift.targeting.application.command;

import com.autolift.targeting.infrastructure.importfile.UpliftScoreCsvImporter;
import org.springframework.stereotype.Component;

@Component
public class ImportUpliftScoresHandler {

  private final UpliftScoreCsvImporter importer;

  public ImportUpliftScoresHandler(UpliftScoreCsvImporter importer) {
    this.importer = importer;
  }

  public int handle(ImportUpliftScoresCommand command) {
    return importer.importFromCsv(command.file(), command.campaignId());
  }
}