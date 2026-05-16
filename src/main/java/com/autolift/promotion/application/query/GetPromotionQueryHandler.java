package com.autolift.promotion.application.query;

import com.autolift.promotion.domain.exception.PromotionNotFoundException;
import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import org.springframework.stereotype.Component;

@Component
public class GetPromotionQueryHandler {

  private final PromotionRepository repository;

  public GetPromotionQueryHandler(PromotionRepository repository) {
    this.repository = repository;
  }

  public PromotionView handle(GetPromotionQuery query) {
    Promotion promotion =
        repository
            .findById(PromotionId.of(query.id()))
            .orElseThrow(() -> new PromotionNotFoundException(query.id()));
    return mapToView(promotion);
  }

  private PromotionView mapToView(Promotion promotion) {
    return PromotionView.builder()
        .id(promotion.getId().getId().toString())
        .name(promotion.getName())
        .description(promotion.getDescription())
        .promotionType(promotion.getPromotionType())
        .value(promotion.getValue())
        .minOrderAmount(promotion.getMinOrderAmount())
        .applicableCustomerSegment(promotion.getApplicableCustomerSegment())
        .status(promotion.getStatus().name())
        .startDate(promotion.getStartDate())
        .endDate(promotion.getEndDate())
        .createdAt(promotion.getCreatedAt())
        .updatedAt(promotion.getUpdatedAt())
        .build();
  }
}
