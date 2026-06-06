package com.autolift.infrastructure.kafka.dto;

import java.time.Instant;

public record CampaignActivatedKafkaEvent(String campaignId, String name, Instant activatedAt) {}
