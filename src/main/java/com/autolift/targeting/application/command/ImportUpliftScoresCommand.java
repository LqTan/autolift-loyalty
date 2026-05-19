package com.autolift.targeting.application.command;

import org.springframework.web.multipart.MultipartFile;

public record ImportUpliftScoresCommand(MultipartFile file, String campaignId) {}
