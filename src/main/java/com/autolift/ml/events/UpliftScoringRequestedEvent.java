package com.autolift.ml.events;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record UpliftScoringRequestedEvent(
    UUID jobId,
    String campaignId,
    String modelVersion,
    Map<String, Object> inputParams,
    Instant requestedAt
) {}