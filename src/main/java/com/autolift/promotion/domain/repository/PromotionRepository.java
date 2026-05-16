package com.autolift.promotion.domain.repository;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.valueobject.PromotionId;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository {

  Optional<Promotion> findById(PromotionId id);

  List<Promotion> findAll();

  Promotion save(Promotion promotion);

  void delete(Promotion promotion);
}
