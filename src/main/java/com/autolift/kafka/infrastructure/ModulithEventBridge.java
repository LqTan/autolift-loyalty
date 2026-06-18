package com.autolift.kafka.infrastructure;

import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.kafka.infrastructure.kafka.events.CampaignActivatedExternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.PointsAddedExternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.PointsDeductedExternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.VoucherRedeemedExternalEvent;
import com.autolift.loyalty.events.PointsAddedEvent;
import com.autolift.loyalty.events.PointsDeductedEvent;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ModulithEventBridge {

  private static final Logger log = LoggerFactory.getLogger(ModulithEventBridge.class);

  private final ApplicationEventPublisher eventPublisher;

  public ModulithEventBridge(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onVoucherRedeemed(VoucherRedeemedEvent event) {
    log.debug("Bridging VoucherRedeemedEvent to external event");
    VoucherRedeemedExternalEvent externalEvent =
        new VoucherRedeemedExternalEvent(
            event.getVoucherId(),
            event.getCode(),
            event.getCampaignId(),
            event.getCustomerId(),
            event.getValue(),
            event.getRedeemedAt());
    eventPublisher.publishEvent(externalEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPointsAdded(PointsAddedEvent event) {
    log.debug("Bridging PointsAddedEvent to external event");
    PointsAddedExternalEvent externalEvent =
        new PointsAddedExternalEvent(
            event.getLoyaltyAccountId().getId(), event.getPoints(), event.getReferenceId());
    eventPublisher.publishEvent(externalEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPointsDeducted(PointsDeductedEvent event) {
    log.debug("Bridging PointsDeductedEvent to external event");
    PointsDeductedExternalEvent externalEvent =
        new PointsDeductedExternalEvent(
            event.getLoyaltyAccountId().getId(), event.getPoints(), event.getReferenceId());
    eventPublisher.publishEvent(externalEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onCampaignActivated(CampaignActivatedEvent event) {
    log.debug("Bridging CampaignActivatedEvent to external event");
    CampaignActivatedExternalEvent externalEvent =
        new CampaignActivatedExternalEvent(event.campaignId(), event.name(), event.activatedAt());
    eventPublisher.publishEvent(externalEvent);
  }
}
