package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.exception.PromotionNotFoundException;
import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.events.PromotionActivatedEvent;
import java.time.Instant;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ActivatePromotionCommandHandler {

  private final PromotionRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public ActivatePromotionCommandHandler(PromotionRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(ActivatePromotionCommand command) {
    Promotion promotion =
        repository
            .findById(PromotionId.of(command.promotionId()))
            .orElseThrow(() -> new PromotionNotFoundException(command.promotionId()));
    promotion.activate();
    repository.save(promotion);
    eventPublisher.publishEvent(new PromotionActivatedEvent(
        promotion.getId().getId().toString(),
        promotion.getName(),
        Instant.now()));
  }
}
