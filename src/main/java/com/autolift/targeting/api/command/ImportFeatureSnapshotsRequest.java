package com.autolift.targeting.api.command;

import org.springframework.web.multipart.MultipartFile;

public record ImportFeatureSnapshotsRequest(MultipartFile file, String campaignId) {}