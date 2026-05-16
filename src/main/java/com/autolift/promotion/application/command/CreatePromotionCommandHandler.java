package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import org.springframework.stereotype.Component;

@Component
public class CreatePromotionCommandHandler {

  private final PromotionRepository repository;

  public CreatePromotionCommandHandler(PromotionRepository repository) {
    this.repository = repository;
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
