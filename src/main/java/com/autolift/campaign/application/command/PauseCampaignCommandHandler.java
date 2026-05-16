package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.exception.CampaignNotFoundException;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignId;
import org.springframework.stereotype.Component;

@Component
public class PauseCampaignCommandHandler {

    private final CampaignRepository repository;

    public PauseCampaignCommandHandler(CampaignRepository repository) {
        this.repository = repository;
    }

    @org.springframework.transaction.annotation.Transactional
    public void handle(PauseCampaignCommand command) {
        Campaign campaign = repository.findById(CampaignId.of(command.campaignId()))
            .orElseThrow(() -> new CampaignNotFoundException(command.campaignId()));
        campaign.pause();
        repository.save(campaign);
    }
}