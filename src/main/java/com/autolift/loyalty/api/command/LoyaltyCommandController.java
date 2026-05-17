package com.autolift.loyalty.api.command;

import com.autolift.loyalty.application.command.AddPointsCommand;
import com.autolift.loyalty.application.command.AddPointsHandler;
import com.autolift.loyalty.application.command.CreateLoyaltyAccountCommand;
import com.autolift.loyalty.application.command.CreateLoyaltyAccountHandler;
import com.autolift.loyalty.application.command.LoyaltyAccountCreatedResult;
import com.autolift.loyalty.application.command.UsePointsCommand;
import com.autolift.loyalty.application.command.UsePointsHandler;
import java.net.URI;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loyalty/accounts")
@Import({
  CreateLoyaltyAccountHandler.class,
  AddPointsHandler.class,
  UsePointsHandler.class
})
public class LoyaltyCommandController {

  private final CreateLoyaltyAccountHandler createHandler;
  private final AddPointsHandler addPointsHandler;
  private final UsePointsHandler usePointsHandler;

  public LoyaltyCommandController(
      CreateLoyaltyAccountHandler createHandler,
      AddPointsHandler addPointsHandler,
      UsePointsHandler usePointsHandler) {
    this.createHandler = createHandler;
    this.addPointsHandler = addPointsHandler;
    this.usePointsHandler = usePointsHandler;
  }

  @PostMapping
  public ResponseEntity<LoyaltyAccountCreatedResult> create(
      @RequestBody CreateLoyaltyAccountRequest request) {
    CreateLoyaltyAccountCommand command = new CreateLoyaltyAccountCommand(request.customerId());
    LoyaltyAccountCreatedResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/loyalty/accounts/" + result.id())).body(result);
  }

  @PostMapping("/{id}/add-points")
  public ResponseEntity<Void> addPoints(@PathVariable String id, @RequestBody AddPointsRequest request) {
    AddPointsCommand command = new AddPointsCommand(id, request.amount(), request.referenceId());
    addPointsHandler.handle(command);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/use-points")
  public ResponseEntity<Void> usePoints(@PathVariable String id, @RequestBody UsePointsRequest request) {
    UsePointsCommand command = new UsePointsCommand(id, request.amount(), request.referenceId());
    usePointsHandler.handle(command);
    return ResponseEntity.noContent().build();
  }
}