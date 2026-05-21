package com.autolift.targeting.infrastructure.importfile;

import com.autolift.targeting.domain.model.CustomerFeatureSnapshot;
import com.autolift.targeting.domain.repository.CustomerFeatureSnapshotRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CustomerFeatureSnapshotCsvImporter {

  private final CustomerFeatureSnapshotRepository repository;

  public CustomerFeatureSnapshotCsvImporter(CustomerFeatureSnapshotRepository repository) {
    this.repository = repository;
  }

  public int importFromCsv(MultipartFile file, String campaignId) {
    try (InputStream is = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      List<CustomerFeatureSnapshot> batch = new ArrayList<>();
      String line = reader.readLine();
      int count = 0;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length < 10) continue;
        CustomerFeatureSnapshot snapshot =
            CustomerFeatureSnapshot.create(
                parts[0].trim(),
                campaignId,
                parseIntOrNull(parts[1]),
                parseIntOrNull(parts[2]),
                parseBigDecimalOrNull(parts[3]),
                parseBigDecimalOrNull(parts[4]),
                parseBigDecimalOrNull(parts[5]),
                parseIntOrNull(parts[6]),
                parseIntOrNull(parts[7]),
                parts.length > 11 ? parts[11].trim() : null,
                parts.length > 9 ? parts[9].trim() : "v1");
        batch.add(snapshot);
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
      throw new RuntimeException("Failed to import feature snapshots CSV", e);
    }
  }

  private Integer parseIntOrNull(String s) {
    try {
      return s.isEmpty() ? null : Integer.parseInt(s.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private BigDecimal parseBigDecimalOrNull(String s) {
    try {
      return s.isEmpty() ? null : new BigDecimal(s.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
