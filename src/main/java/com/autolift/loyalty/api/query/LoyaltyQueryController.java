package com.autolift.loyalty.api.query;

import com.autolift.loyalty.application.query.GetLoyaltyAccountByCustomerIdHandler;
import com.autolift.loyalty.application.query.GetLoyaltyAccountByCustomerIdQuery;
import com.autolift.loyalty.application.query.GetLoyaltyAccountHandler;
import com.autolift.loyalty.application.query.GetLoyaltyAccountQuery;
import com.autolift.loyalty.application.query.LoyaltyAccountView;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loyalty/accounts")
@Import({GetLoyaltyAccountHandler.class, GetLoyaltyAccountByCustomerIdHandler.class})
public class LoyaltyQueryController {

  private final GetLoyaltyAccountHandler getHandler;
  private final GetLoyaltyAccountByCustomerIdHandler getByCustomerIdHandler;

  public LoyaltyQueryController(
      GetLoyaltyAccountHandler getHandler,
      GetLoyaltyAccountByCustomerIdHandler getByCustomerIdHandler) {
    this.getHandler = getHandler;
    this.getByCustomerIdHandler = getByCustomerIdHandler;
  }

  @GetMapping("/{id}")
  public ResponseEntity<LoyaltyAccountResponse> findById(@PathVariable String id) {
    return getHandler
        .handle(new GetLoyaltyAccountQuery(id))
        .map(this::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/customer/{customerId}")
  public ResponseEntity<LoyaltyAccountResponse> findByCustomerId(@PathVariable String customerId) {
    return getByCustomerIdHandler
        .handle(new GetLoyaltyAccountByCustomerIdQuery(customerId))
        .map(this::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  private LoyaltyAccountResponse toResponse(LoyaltyAccountView view) {
    return new LoyaltyAccountResponse(
        view.id(),
        view.customerId(),
        view.pointsBalance(),
        view.tier(),
        view.status(),
        view.createdAt(),
        view.updatedAt());
  }
}