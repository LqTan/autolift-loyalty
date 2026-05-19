package com.autolift.campaign.api.command;

import java.time.Instant;

public record CampaignActivatedResponse(String campaignId, String name, Instant activatedAt) {}
