package com.autolift.loyalty.application.command;

import com.autolift.infrastructure.kafka.KafkaEventPublisher;
import com.autolift.loyalty.domain.exception.LoyaltyAccountNotFoundException;
import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.model.PointTransaction.TransactionType;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.domain.valueobject.PointTransactionId;
import com.autolift.loyalty.events.PointsDeductedEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UsePointsHandler {

  private final LoyaltyAccountRepository repository;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;
  private final KafkaEventPublisher kafkaEventPublisher;

  public UsePointsHandler(
      LoyaltyAccountRepository repository,
      CacheManager cacheManager,
      ApplicationEventPublisher eventPublisher,
      KafkaEventPublisher kafkaEventPublisher) {
    this.repository = repository;
    this.cacheManager = cacheManager;
    this.eventPublisher = eventPublisher;
    this.kafkaEventPublisher = kafkaEventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(UsePointsCommand command) {
    LoyaltyAccount account =
        repository
            .findById(LoyaltyAccountId.of(command.accountId()))
            .orElseThrow(() -> new LoyaltyAccountNotFoundException(command.accountId()));
    account.deductPoints(command.amount());
    repository.save(account);
    PointTransaction transaction =
        PointTransaction.create(
            PointTransactionId.of(account.getId().getId()),
            command.amount().negate(),
            TransactionType.REDEEM,
            command.referenceId());
    repository.saveTransaction(transaction);
    evictCache(account);

    PointsDeductedEvent pointsDeductedEvent =
        new PointsDeductedEvent(account.getId(), command.amount(), command.referenceId());
    eventPublisher.publishEvent(pointsDeductedEvent);
    kafkaEventPublisher.publishPointsDeducted(pointsDeductedEvent);
  }

  private void evictCache(LoyaltyAccount account) {
    var cache = cacheManager.getCache("loyaltyAccounts");
    if (cache != null) {
      cache.evict(account.getId().getId().toString());
      cache.evict(account.getCustomerId());
    }
  }
}
