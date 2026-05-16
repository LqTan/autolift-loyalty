package com.autolift.promotion.api.command;

import com.autolift.promotion.application.command.ActivatePromotionCommand;
import com.autolift.promotion.application.command.ActivatePromotionCommandHandler;
import com.autolift.promotion.application.command.CreatePromotionCommand;
import com.autolift.promotion.application.command.CreatePromotionCommandHandler;
import com.autolift.promotion.application.command.CreatePromotionResult;
import com.autolift.promotion.application.command.DeactivatePromotionCommand;
import com.autolift.promotion.application.command.DeactivatePromotionCommandHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionCommandController {

  private final CreatePromotionCommandHandler createHandler;
  private final ActivatePromotionCommandHandler activateHandler;
  private final DeactivatePromotionCommandHandler deactivateHandler;

  public PromotionCommandController(
      CreatePromotionCommandHandler createHandler,
      ActivatePromotionCommandHandler activateHandler,
      DeactivatePromotionCommandHandler deactivateHandler) {
    this.createHandler = createHandler;
    this.activateHandler = activateHandler;
    this.deactivateHandler = deactivateHandler;
  }

  @PostMapping
  public ResponseEntity<CreatePromotionResult> createPromotion(
      @RequestBody CreatePromotionRequest request) {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            request.name(),
            request.description(),
            request.promotionType(),
            request.value(),
            request.minOrderAmount(),
            request.applicableCustomerSegment(),
            request.startDate(),
            request.endDate());
    CreatePromotionResult result = createHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @PostMapping("/{id}/activate")
  public ResponseEntity<Void> activatePromotion(@PathVariable String id) {
    activateHandler.handle(new ActivatePromotionCommand(id));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivatePromotion(@PathVariable String id) {
    deactivateHandler.handle(new DeactivatePromotionCommand(id));
    return ResponseEntity.noContent().build();
  }
}
