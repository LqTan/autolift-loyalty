package com.autolift.campaign.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CampaignDomainEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public CampaignDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publish(Object event) {
    applicationEventPublisher.publishEvent(event);
  }
}
