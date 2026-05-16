package com.autolift.campaign.events;

import java.time.Instant;

public record CampaignActivatedEvent(String campaignId, String name, Instant activatedAt) {}
