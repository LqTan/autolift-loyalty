package com.autolift.targeting.application.command;

import org.springframework.web.multipart.MultipartFile;

public record ImportFeatureSnapshotsCommand(MultipartFile file, String campaignId) {}