package com.autolift.campaign.application.command;

import com.autolift.campaign.api.command.CreateBatchCampaignsRequest.CampaignBatchItem;
import java.util.List;

public record CreateBatchCampaignsCommand(List<CampaignBatchItem> campaigns) {}
