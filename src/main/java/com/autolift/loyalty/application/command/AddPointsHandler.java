package com.autolift.loyalty.application.command;

import com.autolift.loyalty.domain.exception.LoyaltyAccountNotFoundException;
import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.model.PointTransaction.TransactionType;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.domain.valueobject.PointTransactionId;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class AddPointsHandler {

  private final LoyaltyAccountRepository repository;
  private final CacheManager cacheManager;

  public AddPointsHandler(LoyaltyAccountRepository repository, CacheManager cacheManager) {
    this.repository = repository;
    this.cacheManager = cacheManager;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(AddPointsCommand command) {
    LoyaltyAccount account =
        repository
            .findById(LoyaltyAccountId.of(command.accountId()))
            .orElseThrow(() -> new LoyaltyAccountNotFoundException(command.accountId()));
    account.addPoints(command.amount());
    repository.save(account);
    PointTransaction transaction =
        PointTransaction.create(
            PointTransactionId.of(account.getId().getId()),
            command.amount(),
            TransactionType.EARN,
            command.referenceId());
    repository.saveTransaction(transaction);
    evictCache(account);
  }

  private void evictCache(LoyaltyAccount account) {
    var cache = cacheManager.getCache("loyaltyAccounts");
    if (cache != null) {
      cache.evict(account.getId().getId().toString());
      cache.evict(account.getCustomerId());
    }
  }
}
