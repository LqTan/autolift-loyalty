package com.autolift.targeting.events;

import com.autolift.kafka.infrastructure.kafka.events.CampaignActivatedInternalEvent;
import com.autolift.targeting.application.query.GetTargetCustomersHandler;
import com.autolift.targeting.application.query.GetTargetCustomersQuery;
import com.autolift.targeting.application.query.TargetCustomerView;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CampaignActivatedInternalEventListener {

  private static final Logger log =
      LoggerFactory.getLogger(CampaignActivatedInternalEventListener.class);

  private final GetTargetCustomersHandler targetCustomersHandler;
  private final ApplicationEventPublisher eventPublisher;

  public CampaignActivatedInternalEventListener(
      GetTargetCustomersHandler targetCustomersHandler, ApplicationEventPublisher eventPublisher) {
    this.targetCustomersHandler = targetCustomersHandler;
    this.eventPublisher = eventPublisher;
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onCampaignActivated(CampaignActivatedInternalEvent event) {
    log.info(
        "Received CampaignActivatedInternalEvent for campaign: {} (from Kafka)",
        event.getCampaignId());
    List<TargetCustomerView> candidates =
        targetCustomersHandler.handle(new GetTargetCustomersQuery(event.getCampaignId(), 1000));
    List<String> customerIds = candidates.stream().map(TargetCustomerView::customerId).toList();
    TargetCustomersSelectedEvent selectedEvent =
        new TargetCustomersSelectedEvent(event.getCampaignId(), customerIds, Instant.now());
    eventPublisher.publishEvent(selectedEvent);
    log.info(
        "Published TargetCustomersSelectedEvent with {} customers for campaign {} (from Kafka)",
        customerIds.size(),
        event.getCampaignId());
  }
}
