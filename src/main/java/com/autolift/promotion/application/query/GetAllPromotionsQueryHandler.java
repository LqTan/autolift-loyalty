package com.autolift.promotion.application.query;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetAllPromotionsQueryHandler {

  private final PromotionRepository repository;

  public GetAllPromotionsQueryHandler(PromotionRepository repository) {
    this.repository = repository;
  }

  public List<PromotionView> handle() {
    return repository.findAll().stream().map(this::mapToView).toList();
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
