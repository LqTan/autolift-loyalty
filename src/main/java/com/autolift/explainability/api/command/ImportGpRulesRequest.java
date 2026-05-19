package com.autolift.explainability.api.command;

import org.springframework.web.multipart.MultipartFile;

public record ImportGpRulesRequest(MultipartFile file, String campaignId, String modelVersion) {}
