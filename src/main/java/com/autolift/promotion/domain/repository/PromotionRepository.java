package com.autolift.promotion.domain.repository;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.valueobject.PromotionId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionRepository {

  Optional<Promotion> findById(PromotionId id);

  List<Promotion> findAll();

  Page<Promotion> findAll(Pageable pageable);

  Promotion save(Promotion promotion);

  void delete(Promotion promotion);
}
