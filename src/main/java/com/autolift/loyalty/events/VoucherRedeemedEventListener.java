package com.autolift.loyalty.events;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.model.PointTransaction.TransactionType;
import com.autolift.loyalty.domain.repository.LoyaltyAccountRepository;
import com.autolift.loyalty.domain.valueobject.PointTransactionId;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class VoucherRedeemedEventListener {

  private static final Logger log = LoggerFactory.getLogger(VoucherRedeemedEventListener.class);

  private final LoyaltyAccountRepository loyaltyAccountRepository;
  private final ApplicationEventPublisher eventPublisher;

  public VoucherRedeemedEventListener(
      LoyaltyAccountRepository loyaltyAccountRepository, ApplicationEventPublisher eventPublisher) {
    this.loyaltyAccountRepository = loyaltyAccountRepository;
    this.eventPublisher = eventPublisher;
  }

  @ApplicationModuleListener
  public void onVoucherRedeemed(VoucherRedeemedEvent event) {
    log.info(
        "Voucher redeemed event received: voucherId={}, customerId={}, value={}",
        event.getVoucherId(),
        event.getCustomerId(),
        event.getValue());

    BigDecimal pointsEarned =
        event.getValue().divide(BigDecimal.valueOf(10_000), 0, RoundingMode.FLOOR);

    if (pointsEarned.compareTo(BigDecimal.ZERO) <= 0) {
      log.warn("Voucher value too small to earn points: {}", event.getValue());
      return;
    }

    LoyaltyAccount account =
        loyaltyAccountRepository
            .findByCustomerId(event.getCustomerId())
            .orElseGet(
                () -> {
                  log.info("Creating new loyalty account for customer: {}", event.getCustomerId());
                  LoyaltyAccount newAccount = LoyaltyAccount.create(event.getCustomerId());
                  return loyaltyAccountRepository.save(newAccount);
                });

    account.addPoints(pointsEarned);
    loyaltyAccountRepository.save(account);

    PointTransaction transaction =
        PointTransaction.create(
            PointTransactionId.of(account.getId().getId()),
            pointsEarned,
            TransactionType.EARN,
            event.getVoucherId());
    loyaltyAccountRepository.saveTransaction(transaction);

    log.info(
        "Added {} points to loyalty account for customer {}", pointsEarned, event.getCustomerId());

    eventPublisher.publishEvent(
        new PointsAddedEvent(account.getId(), pointsEarned, event.getVoucherId()));
  }
}
