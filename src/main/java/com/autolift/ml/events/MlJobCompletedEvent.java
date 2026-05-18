package com.autolift.ml.events;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.UUID;

public record MlJobCompletedEvent(
    UUID jobId,
    MlJobType jobType,
    String campaignId,
    String resultPath,
    Instant completedAt
) {}