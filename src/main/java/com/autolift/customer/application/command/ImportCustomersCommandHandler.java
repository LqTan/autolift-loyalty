package com.autolift.customer.application.command;

import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import com.autolift.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import com.autolift.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportCustomersCommandHandler {

  private static final int BATCH_SIZE = 500;
  private static final DateTimeFormatter DT_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final CustomerJpaRepository repository;

  public ImportCustomersCommandHandler(CustomerJpaRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public ImportCustomersResult handle(ImportCustomersCommand command) {
    int imported = 0;
    int failed = 0;
    List<CustomerJpaEntity> batch = new ArrayList<>(BATCH_SIZE);

    try (BufferedReader reader = new BufferedReader(new FileReader(command.filePath()))) {
      String header = reader.readLine();
      if (header == null) {
        return new ImportCustomersResult(0, 0);
      }

      String line;
      while ((line = reader.readLine()) != null) {
        try {
          String[] parts = line.split(",", -1);
          if (parts.length < 8) {
            failed++;
            continue;
          }

          UUID id = UUID.fromString(parts[0].trim());
          String name = parts[2].trim();
          String email = parts[3].trim();
          String phone = parts[4].trim();
          CustomerSegment segment = CustomerSegment.valueOf(parts[5].trim());
          CustomerStatus status = CustomerStatus.valueOf(parts[6].trim());
          java.time.Instant createdAt = parseInstant(parts[7].trim());
          java.time.Instant updatedAt = parseInstant(parts[8].trim());

          CustomerJpaEntity entity =
              new CustomerJpaEntity(id, name, email, phone, segment, status, createdAt, updatedAt);

          batch.add(entity);

          if (batch.size() >= BATCH_SIZE) {
            repository.saveAll(new ArrayList<>(batch));
            imported += batch.size();
            batch.clear();
          }
        } catch (Exception e) {
          failed++;
        }
      }

      if (!batch.isEmpty()) {
        repository.saveAll(batch);
        imported += batch.size();
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to import customers: " + e.getMessage(), e);
    }

    return new ImportCustomersResult(imported, failed);
  }

  private java.time.Instant parseInstant(String value) {
    if (value == null || value.isEmpty()) {
      return java.time.Instant.now();
    }
    try {
      return java.time.Instant.parse(value);
    } catch (Exception e) {
      try {
        LocalDateTime ldt = LocalDateTime.parse(value, DT_FORMAT);
        return ldt.atZone(java.time.ZoneId.systemDefault()).toInstant();
      } catch (Exception ex) {
        return java.time.Instant.now();
      }
    }
  }
}
