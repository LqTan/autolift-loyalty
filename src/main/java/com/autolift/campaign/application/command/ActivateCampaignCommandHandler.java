package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.exception.CampaignNotFoundException;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.campaign.events.CampaignDomainEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ActivateCampaignCommandHandler {

    private final CampaignRepository repository;
    private final CampaignDomainEventPublisher eventPublisher;

    public ActivateCampaignCommandHandler(CampaignRepository repository, CampaignDomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @org.springframework.transaction.annotation.Transactional
    public CampaignActivatedEvent handle(ActivateCampaignCommand command) {
        Campaign campaign = repository.findById(CampaignId.of(command.campaignId()))
            .orElseThrow(() -> new CampaignNotFoundException(command.campaignId()));
        campaign.activate();
        repository.save(campaign);
        CampaignActivatedEvent event = new CampaignActivatedEvent(
            campaign.getId().getId().toString(),
            campaign.getName(),
            Instant.now()
        );
        eventPublisher.publish(event);
        return event;
    }
}