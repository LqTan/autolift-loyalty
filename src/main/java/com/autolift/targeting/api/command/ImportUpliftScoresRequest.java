package com.autolift.targeting.api.command;

import org.springframework.web.multipart.MultipartFile;

public record ImportUpliftScoresRequest(MultipartFile file, String campaignId) {}