package com.autolift.ml.application.command;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.util.Map;
import java.util.UUID;

public class CreateMlJobCommand {
  private final MlJobType jobType;
  private final String campaignId;
  private final String modelVersion;
  private final Map<String, Object> inputParams;
  private final UUID upliftScoreJobId;

  public CreateMlJobCommand(
      MlJobType jobType,
      String campaignId,
      String modelVersion,
      Map<String, Object> inputParams,
      UUID upliftScoreJobId) {
    this.jobType = jobType;
    this.campaignId = campaignId;
    this.modelVersion = modelVersion;
    this.inputParams = inputParams;
    this.upliftScoreJobId = upliftScoreJobId;
  }

  public MlJobType getJobType() {
    return jobType;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public Map<String, Object> getInputParams() {
    return inputParams;
  }

  public UUID getUpliftScoreJobId() {
    return upliftScoreJobId;
  }
}
