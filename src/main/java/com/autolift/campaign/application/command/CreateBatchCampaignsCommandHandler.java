package com.autolift.campaign.application.command;

import com.autolift.campaign.api.command.CreateBatchCampaignsRequest.CampaignBatchItem;
import com.autolift.campaign.api.command.CreateBatchCampaignsResponse;
import com.autolift.campaign.api.command.CreateBatchCampaignsResponse.BatchCampaignResult;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.Budget;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateBatchCampaignsCommandHandler {

  private final CampaignRepository repository;

  public CreateBatchCampaignsCommandHandler(CampaignRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public CreateBatchCampaignsResponse handle(CreateBatchCampaignsCommand command) {
    List<BatchCampaignResult> results =
        command.campaigns().stream()
            .map(this::createCampaign)
            .toList();

    return new CreateBatchCampaignsResponse(results);
  }

  private BatchCampaignResult createCampaign(CampaignBatchItem item) {
    Budget budget = Budget.of(item.budgetAmount(), item.budgetCurrency());
    Campaign campaign =
        Campaign.create(
            item.name(),
            item.description(),
            item.startDate(),
            item.endDate(),
            budget);
    repository.save(campaign);
    return new BatchCampaignResult(
        campaign.getId().getId().toString(),
        campaign.getName(),
        campaign.getStatus().name());
  }
}