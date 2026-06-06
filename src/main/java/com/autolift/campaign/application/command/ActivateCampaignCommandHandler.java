package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.exception.CampaignNotFoundException;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.infrastructure.kafka.KafkaEventPublisher;
import java.time.Instant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ActivateCampaignCommandHandler {

  private final CampaignRepository repository;
  private final ApplicationEventPublisher eventPublisher;
  private final KafkaEventPublisher kafkaEventPublisher;

  public ActivateCampaignCommandHandler(
      CampaignRepository repository,
      ApplicationEventPublisher eventPublisher,
      KafkaEventPublisher kafkaEventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
    this.kafkaEventPublisher = kafkaEventPublisher;
  }

  @Caching(
      evict = {
        @CacheEvict(value = "campaigns", key = "#command.campaignId()"),
        @CacheEvict(value = "campaigns", key = "'all'")
      })
  @org.springframework.transaction.annotation.Transactional
  public CampaignActivatedEvent handle(ActivateCampaignCommand command) {
    Campaign campaign =
        repository
            .findById(CampaignId.of(command.campaignId()))
            .orElseThrow(() -> new CampaignNotFoundException(command.campaignId()));
    campaign.activate();
    repository.save(campaign);
    CampaignActivatedEvent event =
        new CampaignActivatedEvent(
            campaign.getId().getId().toString(), campaign.getName(), Instant.now());
    eventPublisher.publishEvent(event);
    kafkaEventPublisher.publishCampaignActivated(event);
    return event;
  }
}
