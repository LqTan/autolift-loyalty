package com.autolift.ml.application.command;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateMlJobHandler {

  private static final Logger log = LoggerFactory.getLogger(CreateMlJobHandler.class);

  private final MlJobRepository mlJobRepository;

  @Value("${ml.conda.env:autolift-ml}")
  private String condaEnv;

  @Value("${ml.worker.script:ml/scripts/run_ml_worker.py}")
  private String workerScript;

  @Value("${ml.conda.path:/home/archer/miniforge3}")
  private String condaPath;

  public CreateMlJobHandler(MlJobRepository mlJobRepository) {
    this.mlJobRepository = mlJobRepository;
  }

  public MlJob handle(CreateMlJobCommand command) {
    log.info(
        "Creating ML job: type={}, campaignId={}", command.getJobType(), command.getCampaignId());

    MlJob job;
    if (command.getJobType() == MlJobType.GP_RULE_EXTRACTION) {
      job =
          MlJob.createGpRuleExtractionJob(
              command.getCampaignId(),
              command.getModelVersion(),
              command.getInputParams(),
              command.getUpliftScoreJobId());
    } else {
      job =
          MlJob.createUpliftScoringJob(
              command.getCampaignId(), command.getModelVersion(), command.getInputParams());
    }

    MlJob savedJob = mlJobRepository.save(job);
    log.info("Created ML job: id={}, status=PENDING", savedJob.getId().getId());

    triggerPythonWorkerAsync(savedJob.getId().getId());

    return savedJob;
  }

  public void triggerPythonWorkerAsync(java.util.UUID jobId) {
    new Thread(
            () -> {
              try {
                log.info("Triggering Python ML worker for job: {}", jobId);

                String baseDir = System.getProperty("user.dir");
                String scriptPath = baseDir + "/" + workerScript;
                String condaActivate = condaPath + "/bin/activate";

                String dbHost =
                    System.getenv("POSTGRES_HOST") != null
                        ? System.getenv("POSTGRES_HOST")
                        : "localhost";
                String dbName =
                    System.getenv("POSTGRES_DB") != null
                        ? System.getenv("POSTGRES_DB")
                        : "mydatabase";
                String dbUser =
                    System.getenv("POSTGRES_USER") != null
                        ? System.getenv("POSTGRES_USER")
                        : "myuser";
                String dbPass =
                    System.getenv("POSTGRES_PASSWORD") != null
                        ? System.getenv("POSTGRES_PASSWORD")
                        : "secret";

                String dbUrl =
                    "postgresql://" + dbUser + ":" + dbPass + "@" + dbHost + ":5432/" + dbName;

                String cmd =
                    "source "
                        + condaActivate
                        + " "
                        + condaEnv
                        + " && python "
                        + scriptPath
                        + " --db-url '"
                        + dbUrl
                        + "'";
                log.info("[Python Worker] Full command: {}", cmd);

                ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", cmd);
                pb.directory(new java.io.File(baseDir));
                pb.redirectErrorStream(true);
                pb.redirectOutput(java.io.File.createTempFile("ml_worker", ".log"));

                Process process = pb.start();
                int exitCode = process.waitFor();
                log.info("Python worker exited with code: {}", exitCode);

              } catch (Exception e) {
                log.error("Failed to trigger Python worker: {}", e.getMessage(), e);
              }
            })
        .start();
  }
}
