package com.autolift.explainability.infrastructure.importfile;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GpRuleCsvImporter {

  private final GpRuleRepository repository;

  public GpRuleCsvImporter(GpRuleRepository repository) {
    this.repository = repository;
  }

  public int importFromCsv(MultipartFile file, String campaignId, String modelVersion) {
    try (InputStream is = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      List<GpRule> batch = new ArrayList<>();
      String line = reader.readLine();
      int count = 0;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",", -1);
        if (parts.length < 11) continue;
        GpRule rule = GpRule.create(
            parts[0].trim(),
            parts[1].trim(),
            parts[2].trim(),
            "PERSUADABLE",
            parseBigDecimal(parts[4]),
            parseBigDecimal(parts[5]),
            parseBigDecimal(parts[6]),
            parseBigDecimal(parts[7]),
            parseBigDecimal(parts[8]),
            parts[9].trim(),
            parts[10].trim()
        );
        batch.add(rule);
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
      return count;
    } catch (Exception e) {
      throw new RuntimeException("Failed to import GP rules CSV", e);
    }
  }

  private BigDecimal parseBigDecimal(String s) {
    try {
      return new BigDecimal(s.trim());
    } catch (NumberFormatException e) {
      return BigDecimal.ZERO;
    }
  }
}