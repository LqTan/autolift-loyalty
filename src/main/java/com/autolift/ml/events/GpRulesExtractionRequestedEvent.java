package com.autolift.ml.events;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record GpRulesExtractionRequestedEvent(
    UUID jobId,
    String campaignId,
    UUID upliftScoreJobId,
    String modelVersion,
    Map<String, Object> inputParams,
    Instant requestedAt
) {}