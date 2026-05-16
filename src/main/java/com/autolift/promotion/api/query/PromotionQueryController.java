package com.autolift.promotion.api.query;

import com.autolift.promotion.application.query.GetAllPromotionsQueryHandler;
import com.autolift.promotion.application.query.GetPromotionQuery;
import com.autolift.promotion.application.query.GetPromotionQueryHandler;
import com.autolift.promotion.application.query.PromotionView;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionQueryController {

  private final GetPromotionQueryHandler getByIdHandler;
  private final GetAllPromotionsQueryHandler getAllHandler;

  public PromotionQueryController(
      GetPromotionQueryHandler getByIdHandler, GetAllPromotionsQueryHandler getAllHandler) {
    this.getByIdHandler = getByIdHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping("/{id}")
  public ResponseEntity<PromotionView> getPromotion(@PathVariable String id) {
    PromotionView view = getByIdHandler.handle(new GetPromotionQuery(id));
    return ResponseEntity.ok(view);
  }

  @GetMapping
  public ResponseEntity<List<PromotionView>> getAllPromotions() {
    List<PromotionView> promotions = getAllHandler.handle();
    return ResponseEntity.ok(promotions);
  }
}
