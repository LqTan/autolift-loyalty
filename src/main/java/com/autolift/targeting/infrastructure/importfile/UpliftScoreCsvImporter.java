package com.autolift.targeting.infrastructure.importfile;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.repository.CustomerUpliftScoreRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UpliftScoreCsvImporter {

  private final CustomerUpliftScoreRepository repository;

  public UpliftScoreCsvImporter(CustomerUpliftScoreRepository repository) {
    this.repository = repository;
  }

  public int importFromCsv(MultipartFile file, String campaignId) {
    try (InputStream is = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      List<CustomerUpliftScore> batch = new ArrayList<>();
      String line = reader.readLine();
      int count = 0;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length < 8) continue;
        CustomerUpliftScore score =
            CustomerUpliftScore.create(
                parts[0].trim(),
                campaignId,
                new BigDecimal(parts[1].trim()),
                new BigDecimal(parts[2].trim()),
                new BigDecimal(parts[3].trim()),
                parts[6].trim());
        batch.add(score);
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
      throw new RuntimeException("Failed to import uplift scores CSV", e);
    }
  }
}
