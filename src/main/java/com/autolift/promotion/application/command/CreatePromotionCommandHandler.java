package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.events.DomainEventPublisher;
import com.autolift.promotion.events.PromotionCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CreatePromotionCommandHandler {

  private final PromotionRepository repository;
  private final DomainEventPublisher eventPublisher;

  public CreatePromotionCommandHandler(PromotionRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public CreatePromotionResult handle(CreatePromotionCommand command) {
    Promotion promotion =
        Promotion.create(
            command.name(),
            command.description(),
            command.promotionType(),
            command.value(),
            command.minOrderAmount(),
            command.applicableCustomerSegment(),
            command.startDate(),
            command.endDate());
    repository.save(promotion);
    eventPublisher.publish(new PromotionCreatedEvent(
        promotion.getId().getId().toString(),
        promotion.getName(),
        promotion.getCreatedAt()));
    return new CreatePromotionResult(
        promotion.getId().getId().toString(),
        promotion.getName(),
        promotion.getDescription(),
        promotion.getPromotionType(),
        promotion.getValue(),
        promotion.getMinOrderAmount(),
        promotion.getApplicableCustomerSegment(),
        promotion.getStatus().name(),
        promotion.getStartDate(),
        promotion.getEndDate());
  }
}
