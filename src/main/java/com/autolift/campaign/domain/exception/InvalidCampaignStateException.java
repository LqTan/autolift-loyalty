package com.autolift.campaign.domain.exception;

public class InvalidCampaignStateException extends RuntimeException {

    public InvalidCampaignStateException(String message) {
        super(message);
    }
}