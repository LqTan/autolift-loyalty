package com.autolift.promotion.application.command;

import com.autolift.promotion.domain.exception.PromotionNotFoundException;
import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import org.springframework.stereotype.Component;

@Component
public class DeactivatePromotionCommandHandler {

  private final PromotionRepository repository;

  public DeactivatePromotionCommandHandler(PromotionRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(DeactivatePromotionCommand command) {
    Promotion promotion =
        repository
            .findById(PromotionId.of(command.promotionId()))
            .orElseThrow(() -> new PromotionNotFoundException(command.promotionId()));
    promotion.deactivate();
    repository.save(promotion);
  }
}
