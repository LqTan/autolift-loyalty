package com.autolift.kafka.infrastructure.kafka.events;

import java.time.Instant;
import org.springframework.modulith.events.Externalized;

@Externalized("campaign.activated::#{#this.campaignId}")
public record CampaignActivatedExternalEvent(String campaignId, String name, Instant activatedAt) {}
