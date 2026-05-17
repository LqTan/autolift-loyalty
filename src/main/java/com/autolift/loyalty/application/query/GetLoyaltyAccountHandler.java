package com.autolift.loyalty.application.query;

import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class GetLoyaltyAccountHandler {

  private final LoyaltyAccountRepository repository;

  public GetLoyaltyAccountHandler(LoyaltyAccountRepository repository) {
    this.repository = repository;
  }

  public Optional<LoyaltyAccountView> handle(GetLoyaltyAccountQuery query) {
    return repository
        .findById(LoyaltyAccountId.of(query.id()))
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