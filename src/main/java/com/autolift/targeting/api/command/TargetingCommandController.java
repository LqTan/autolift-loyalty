package com.autolift.targeting.api.command;

import com.autolift.targeting.application.command.ImportFeatureSnapshotsCommand;
import com.autolift.targeting.application.command.ImportFeatureSnapshotsHandler;
import com.autolift.targeting.application.command.ImportUpliftScoresCommand;
import com.autolift.targeting.application.command.ImportUpliftScoresHandler;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/targeting")
@Import({ImportUpliftScoresHandler.class, ImportFeatureSnapshotsHandler.class})
public class TargetingCommandController {

  private final ImportUpliftScoresHandler upliftScoresHandler;
  private final ImportFeatureSnapshotsHandler featureSnapshotsHandler;

  public TargetingCommandController(
      ImportUpliftScoresHandler upliftScoresHandler,
      ImportFeatureSnapshotsHandler featureSnapshotsHandler) {
    this.upliftScoresHandler = upliftScoresHandler;
    this.featureSnapshotsHandler = featureSnapshotsHandler;
  }

  @PostMapping("/scores/import")
  public ResponseEntity<ImportResult> importUpliftScores(
      @RequestParam("file") MultipartFile file,
      @RequestParam("campaignId") String campaignId) {
    int count = upliftScoresHandler.handle(new ImportUpliftScoresCommand(file, campaignId));
    return ResponseEntity.ok(new ImportResult(campaignId, count, "uplift_scores"));
  }

  @PostMapping("/features/import")
  public ResponseEntity<ImportResult> importFeatureSnapshots(
      @RequestParam("file") MultipartFile file,
      @RequestParam("campaignId") String campaignId) {
    int count = featureSnapshotsHandler.handle(new ImportFeatureSnapshotsCommand(file, campaignId));
    return ResponseEntity.ok(new ImportResult(campaignId, count, "feature_snapshots"));
  }

  public record ImportResult(String campaignId, int recordsImported, String type) {}
}