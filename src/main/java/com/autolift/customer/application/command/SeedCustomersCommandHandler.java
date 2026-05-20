package com.autolift.customer.application.command;

import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import com.autolift.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import com.autolift.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeedCustomersCommandHandler {

  private static final Logger log = LoggerFactory.getLogger(SeedCustomersCommandHandler.class);
  private static final int BATCH_SIZE = 500;

  private final CustomerJpaRepository repository;
  private final String mlDataPath;

  public SeedCustomersCommandHandler(
      CustomerJpaRepository repository, @Value("${ml.data.path:ml/data}") String mlDataPath) {
    this.repository = repository;
    this.mlDataPath = mlDataPath;
  }

  @Async
  @Transactional
  public CompletableFuture<ImportCustomersResult> handle(SeedCustomersCommand command) {
    log.info("Starting X5 customers seed process (async)...");
    int imported = 0;
    int failed = 0;

    try (BufferedReader reader = openClientsCsv()) {
      String header = reader.readLine();
      if (header == null) {
        return CompletableFuture.completedFuture(new ImportCustomersResult(0, 0));
      }

      String line;
      var batch = new ArrayList<CustomerJpaEntity>(BATCH_SIZE);

      while ((line = reader.readLine()) != null) {
        try {
          String[] parts = line.split(",", -1);
          if (parts.length < 1) {
            failed++;
            continue;
          }

          String customerId = parts[0].trim().replace("\"", "");
          UUID id = customerIdToUuid(customerId);
          String name = "X5 Customer " + customerId.substring(0, Math.min(8, customerId.length()));
          String email = customerId + "@x5.client";

          CustomerJpaEntity entity =
              new CustomerJpaEntity(
                  id,
                  name,
                  email,
                  "",
                  CustomerSegment.NORMAL,
                  CustomerStatus.ACTIVE,
                  Instant.now(),
                  Instant.now());

          batch.add(entity);

          if (batch.size() >= BATCH_SIZE) {
            repository.saveAll(new ArrayList<>(batch));
            imported += batch.size();
            log.info("Imported {} customers so far...", imported);
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

      log.info("Seed complete: {} imported, {} failed", imported, failed);
    } catch (Exception e) {
      log.error("Failed to seed customers: {}", e.getMessage(), e);
      return CompletableFuture.failedFuture(e);
    }

    return CompletableFuture.completedFuture(new ImportCustomersResult(imported, failed));
  }

  private BufferedReader openClientsCsv() throws Exception {
    String mlDataDir = mlDataPath.endsWith("/") ? mlDataPath : mlDataPath + "/";
    java.nio.file.Path clientsGzPath = java.nio.file.Paths.get(mlDataDir + "clients.csv.gz");

    if (!java.nio.file.Files.exists(clientsGzPath)) {
      throw new RuntimeException(
          "Cannot find X5 clients.csv.gz at: "
              + clientsGzPath
              + ". Please ensure ml/data/clients.csv.gz exists.");
    }

    var gzipStream =
        new java.util.zip.GZIPInputStream(java.nio.file.Files.newInputStream(clientsGzPath));
    return new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));
  }

  private UUID customerIdToUuid(String customerId) {
    UUID namespaceUuid = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
    return UUID.nameUUIDFromBytes(("6ba7b810-9dad-11d1-80b4-00c04fd430c8" + customerId).getBytes());
  }
}
