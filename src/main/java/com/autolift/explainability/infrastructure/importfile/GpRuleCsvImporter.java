package com.autolift.explainability.infrastructure.importfile;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GpRuleCsvImporter {

  private static final Logger log = LoggerFactory.getLogger(GpRuleCsvImporter.class);

  private final GpRuleRepository repository;

  public GpRuleCsvImporter(GpRuleRepository repository) {
    this.repository = repository;
  }

  public int importFromCsv(MultipartFile file, String campaignId, String modelVersion) {
    try (InputStream is = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      return doImport(reader, campaignId, modelVersion);
    } catch (Exception e) {
      throw new RuntimeException("Failed to import GP rules CSV", e);
    }
  }

  public int importFromFilePath(String filePath, String campaignId, String modelVersion) {
    log.info(
        ">>> GpRuleCsvImporter: filePath={}, campaignId={}, modelVersion={}",
        filePath,
        campaignId,
        modelVersion);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      return doImport(reader, campaignId, modelVersion);
    } catch (Exception e) {
      throw new RuntimeException("Failed to import GP rules from file: " + filePath, e);
    }
  }

  private int doImport(BufferedReader reader, String campaignId, String modelVersion)
      throws Exception {
    List<GpRule> batch = new ArrayList<>();
    String line = reader.readLine();
    log.info(">>> GpRuleCsvImporter header: {}", line);
    int count = 0;
    int skipped = 0;
    while ((line = reader.readLine()) != null) {
      if (line.trim().isEmpty()) continue;
      String[] parts = line.split(",", -1);
      if (parts.length < 8) {
        skipped++;
        continue;
      }
      try {
        GpRule rule =
            GpRule.create(
                parts[0].trim(),
                parts[1].trim(),
                parts.length > 2 ? parts[2].trim() : parts[1].trim(),
                parts.length > 3 ? parts[3].trim() : "PERSUADABLE",
                parseBigDecimal(parts.length > 4 ? parts[4] : "0"),
                parseBigDecimal(parts.length > 5 ? parts[5] : "0"),
                parseBigDecimal(parts.length > 6 ? parts[6] : "0"),
                parseBigDecimal(parts.length > 7 ? parts[7] : "0"),
                parseBigDecimal(parts.length > 8 ? parts[8] : "0"),
                modelVersion,
                parts.length > 9 ? parts[9].trim() : "gp_rules.csv");
        batch.add(rule);
      } catch (Exception e) {
        log.error(">>> Failed to parse line: {}", line, e);
      }
      if (batch.size() >= 500) {
        repository.saveAll(batch);
        count += batch.size();
        batch.clear();
      }
    }
    if (!batch.isEmpty()) {
      repository.saveAll(batch);
      count += batch.size();
    }
    log.info(
        ">>> GpRuleCsvImporter done: imported={}, skipped={}, campaignId={}",
        count,
        skipped,
        campaignId);
    return count;
  }

  private BigDecimal parseBigDecimal(String s) {
    try {
      return new BigDecimal(s.trim());
    } catch (NumberFormatException e) {
      return BigDecimal.ZERO;
    }
  }
}
