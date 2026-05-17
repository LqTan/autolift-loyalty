package com.autolift.loyalty.application.command;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateLoyaltyAccountHandler {

  private final LoyaltyAccountRepository repository;

  public CreateLoyaltyAccountHandler(LoyaltyAccountRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public LoyaltyAccountCreatedResult handle(CreateLoyaltyAccountCommand command) {
    LoyaltyAccount account = LoyaltyAccount.create(command.customerId());
    repository.save(account);
    return new LoyaltyAccountCreatedResult(
        account.getId().getId().toString(),
        account.getCustomerId(),
        account.getPointsBalance(),
        account.getTier().name(),
        account.getStatus().name(),
        account.getCreatedAt());
  }
}