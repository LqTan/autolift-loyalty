package com.autolift.customer.api.command;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema
public record SeedCustomersResult(
    @Schema(description = "Job ID to track progress", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID jobId,
    @Schema(description = "Job status", example = "PENDING")
    String status) {}