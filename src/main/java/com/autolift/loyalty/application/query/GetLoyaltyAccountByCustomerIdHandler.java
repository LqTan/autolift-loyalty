package com.autolift.loyalty.application.query;

import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GetLoyaltyAccountByCustomerIdHandler {

  private final LoyaltyAccountRepository repository;

  public GetLoyaltyAccountByCustomerIdHandler(LoyaltyAccountRepository repository) {
    this.repository = repository;
  }

  public Optional<LoyaltyAccountView> handle(GetLoyaltyAccountByCustomerIdQuery query) {
    return repository
        .findByCustomerId(query.customerId())
        .map(
            account ->
                new LoyaltyAccountView(
                    account.getId().getId().toString(),
                    account.getCustomerId(),
                    account.getPointsBalance(),
                    account.getTier().name(),
                    account.getStatus().name(),
                    account.getCreatedAt(),
                    account.getUpdatedAt()));
  }
}
