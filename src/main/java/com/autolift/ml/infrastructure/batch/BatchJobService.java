package com.autolift.ml.infrastructure.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BatchJobService {

  private static final Logger log = LoggerFactory.getLogger(BatchJobService.class);

  private final JobLauncher jobLauncher;
  private final Job customerSeedJob;
  private final String mlDataPath;

  public BatchJobService(
      JobLauncher jobLauncher,
      @Qualifier("customerSeedJob") Job customerSeedJob,
      @Value("${ml.data.path:ml/data}") String mlDataPath) {
    this.jobLauncher = jobLauncher;
    this.customerSeedJob = customerSeedJob;
    this.mlDataPath = mlDataPath;
  }

  @Async
  public void launchCustomerSeedJob(String jobId) {
    try {
      String gzPath = mlDataPath.endsWith("/") ? mlDataPath + "clients.csv.gz" : mlDataPath + "/clients.csv.gz";
      File csvFile = decompressGzip(gzPath, jobId);

      log.info("Launching customer seed batch job: jobId={}, filePath={}", jobId, csvFile.getAbsolutePath());

      JobParameters jobParameters = new JobParametersBuilder()
          .addString("jobId", jobId)
          .addString("filePath", csvFile.getAbsolutePath())
          .addLong("timestamp", System.currentTimeMillis())
          .toJobParameters();

      jobLauncher.run(customerSeedJob, jobParameters);

      log.info("Customer seed batch job completed: jobId={}", jobId);

      if (!csvFile.delete()) {
        log.warn("Failed to delete temp file: {}", csvFile.getAbsolutePath());
      }
    } catch (Exception e) {
      log.error("Failed to launch customer seed batch job: jobId={}", jobId, e);
    }
  }

  private File decompressGzip(String gzPath, String jobId) throws Exception {
    File tempFile = File.createTempFile("customers_" + jobId + "_", ".csv");

    try (GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(gzPath));
         BufferedReader reader = new BufferedReader(new InputStreamReader(gzipStream, StandardCharsets.UTF_8));
         FileOutputStream fos = new FileOutputStream(tempFile)) {

      String line;
      while ((line = reader.readLine()) != null) {
        fos.write((line + "\n").getBytes(StandardCharsets.UTF_8));
      }
    }

    log.info("Decompressed {} -> {} ({} bytes)", gzPath, tempFile.getAbsolutePath(), tempFile.length());
    return tempFile;
  }
}