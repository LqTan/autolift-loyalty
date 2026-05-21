package com.autolift.explainability.application.command;

public record ImportGpRulesFromFileCommand(
    String filePath, String campaignId, String modelVersion) {}
