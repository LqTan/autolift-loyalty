package com.autolift.explainability.api.command;

import com.autolift.explainability.application.command.ImportGpRulesCommand;
import com.autolift.explainability.application.command.ImportGpRulesHandler;
import com.autolift.explainability.infrastructure.importfile.GpRuleCsvImporter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/explainability")
public class ExplainabilityCommandController {

  private final ImportGpRulesHandler importHandler;
  private final GpRuleCsvImporter csvImporter;

  public ExplainabilityCommandController(
      ImportGpRulesHandler importHandler,
      GpRuleCsvImporter csvImporter) {
    this.importHandler = importHandler;
    this.csvImporter = csvImporter;
  }

  @PostMapping("/gp-rules/import")
  public ResponseEntity<Integer> importGpRules(
      @RequestParam("file") MultipartFile file,
      @RequestParam("campaignId") String campaignId,
      @RequestParam(value = "modelVersion", defaultValue = "v1") String modelVersion) {
    int count = csvImporter.importFromCsv(file, campaignId, modelVersion);
    return ResponseEntity.ok(count);
  }
}