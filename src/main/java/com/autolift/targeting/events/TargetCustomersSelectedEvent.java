package com.autolift.targeting.events;

import java.time.Instant;
import java.util.List;

public record TargetCustomersSelectedEvent(
    String campaignId,
    List<String> customerIds,
    Instant selectedAt) {}