package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.Budget;
import org.springframework.stereotype.Component;

@Component
public class CreateCampaignCommandHandler {

  private final CampaignRepository repository;

  public CreateCampaignCommandHandler(CampaignRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public CampaignCreatedResult handle(CreateCampaignCommand command) {
    Budget budget = Budget.of(command.budgetAmount(), command.budgetCurrency());
    Campaign campaign =
        Campaign.create(
            command.name(), command.description(), command.startDate(), command.endDate(), budget);
    repository.save(campaign);
    return new CampaignCreatedResult(
        campaign.getId().getId().toString(),
        campaign.getName(),
        campaign.getDescription(),
        campaign.getStatus().name(),
        campaign.getStartDate(),
        campaign.getEndDate(),
        campaign.getBudget().getAmount(),
        campaign.getBudget().getCurrency());
  }
}
