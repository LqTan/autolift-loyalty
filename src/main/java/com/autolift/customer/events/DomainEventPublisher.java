package com.autolift.customer.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("customerEventPublisher")
public class DomainEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  public void publish(Object event) {
    applicationEventPublisher.publishEvent(event);
  }
}