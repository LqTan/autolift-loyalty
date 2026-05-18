package com.autolift.ml.events;

import com.autolift.ml.domain.valueobject.MlJobType;
import java.time.Instant;
import java.util.UUID;

public record MlJobFailedEvent(
    UUID jobId,
    MlJobType jobType,
    String campaignId,
    String errorMessage,
    Instant failedAt
) {}