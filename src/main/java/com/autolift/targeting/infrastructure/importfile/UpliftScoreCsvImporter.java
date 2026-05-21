package com.autolift.targeting.infrastructure.importfile;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.repository.CustomerUpliftScoreRepository;
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
public class UpliftScoreCsvImporter {

  private static final Logger log = LoggerFactory.getLogger(UpliftScoreCsvImporter.class);

  private final CustomerUpliftScoreRepository repository;

  public UpliftScoreCsvImporter(CustomerUpliftScoreRepository repository) {
    this.repository = repository;
  }

  public int importFromCsv(MultipartFile file, String campaignId) {
    try (InputStream is = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      return doImport(reader, campaignId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to import uplift scores CSV", e);
    }
  }

  public int importFromFilePath(String filePath, String campaignId) {
    log.info(">>> ImportCSV: filePath={}, campaignId={}", filePath, campaignId);
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      repository.deleteByCampaignId(campaignId);
      return doImport(reader, campaignId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to import uplift scores from file: " + filePath, e);
    }
  }

  private int doImport(BufferedReader reader, String campaignId) throws Exception {
    List<CustomerUpliftScore> batch = new ArrayList<>();
    String line = reader.readLine();
    log.info(">>> doImport: header line = {}", line);
    int count = 0;
    int skipped = 0;
    while ((line = reader.readLine()) != null) {
      String[] parts = line.split(",");
      if (parts.length < 8) {
        skipped++;
        continue;
      }
      try {
        CustomerUpliftScore score =
            CustomerUpliftScore.create(
                parts[0].trim(),
                campaignId,
                new BigDecimal(parts[2].trim()),
                new BigDecimal(parts[3].trim()),
                new BigDecimal(parts[4].trim()),
                parts[6].trim());
        batch.add(score);
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
        ">>> doImport done: imported={}, skipped={}, campaignId={}", count, skipped, campaignId);
    return count;
  }
}
