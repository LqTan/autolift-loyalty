package com.autolift.ml.api.command;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.util.Map;

public class CreateMlJobRequest {
  private MlJobType jobType;
  private String campaignId;
  private String modelVersion;
  private Map<String, Object> inputParams;
  private String upliftScoreJobId;

  protected CreateMlJobRequest() {}

  public CreateMlJobRequest(
      MlJobType jobType,
      String campaignId,
      String modelVersion,
      Map<String, Object> inputParams,
      String upliftScoreJobId) {
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

  public String getUpliftScoreJobId() {
    return upliftScoreJobId;
  }
}
