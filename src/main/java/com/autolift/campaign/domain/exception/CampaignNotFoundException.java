package com.autolift.campaign.domain.exception;

public class CampaignNotFoundException extends RuntimeException {

  public CampaignNotFoundException(String id) {
    super("Campaign not found with id: " + id);
  }
}
