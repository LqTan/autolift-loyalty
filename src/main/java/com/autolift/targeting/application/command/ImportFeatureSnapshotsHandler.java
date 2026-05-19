package com.autolift.targeting.application.command;

import com.autolift.targeting.infrastructure.importfile.CustomerFeatureSnapshotCsvImporter;
import org.springframework.stereotype.Component;

@Component
public class ImportFeatureSnapshotsHandler {

  private final CustomerFeatureSnapshotCsvImporter importer;

  public ImportFeatureSnapshotsHandler(CustomerFeatureSnapshotCsvImporter importer) {
    this.importer = importer;
  }

  public int handle(ImportFeatureSnapshotsCommand command) {
    return importer.importFromCsv(command.file(), command.campaignId());
  }
}
