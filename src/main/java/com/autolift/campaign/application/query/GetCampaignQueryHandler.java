package com.autolift.campaign.application.query;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetCampaignQueryHandler {

    private final CampaignReadRepository readRepository;

    public GetCampaignQueryHandler(CampaignReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public Optional<CampaignView> handle(GetCampaignQuery query) {
        return readRepository.findById(query.campaignId());
    }
}