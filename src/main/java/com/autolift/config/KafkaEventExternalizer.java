package com.autolift.config;

import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.infrastructure.kafka.dto.CampaignActivatedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.PointsAddedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.PointsDeductedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.VoucherRedeemedKafkaEvent;
import com.autolift.loyalty.events.PointsAddedEvent;
import com.autolift.loyalty.events.PointsDeductedEvent;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class KafkaEventExternalizer {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventExternalizer.class);

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final Map<Class<? extends ApplicationEvent>, EventRouting> routingRules = new HashMap<>();

  public KafkaEventExternalizer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
    registerRoutingRules();
  }

  private void registerRoutingRules() {
    routingRules.put(
        VoucherRedeemedEvent.class,
        new EventRouting(
            KafkaConfig.VOUCHER_REDEEMED_TOPIC,
            e -> {
              var event = (VoucherRedeemedEvent) e;
              return event.getVoucherId();
            }));

    routingRules.put(
        PointsAddedEvent.class,
        new EventRouting(
            KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC,
            e -> {
              var event = (PointsAddedEvent) e;
              return event.getLoyaltyAccountId().getId().toString();
            }));

    routingRules.put(
        PointsDeductedEvent.class,
        new EventRouting(
            KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC,
            e -> {
              var event = (PointsDeductedEvent) e;
              return event.getLoyaltyAccountId().getId().toString();
            }));

    routingRules.put(
        CampaignActivatedEvent.class,
        new EventRouting(
            KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC,
            e -> {
              var event = (CampaignActivatedEvent) e;
              return event.campaignId();
            }));
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onApplicationEvent(ApplicationEvent event) {
    EventRouting routing = routingRules.get(event.getClass());
    if (routing == null) {
      log.debug("No Kafka routing configured for event: {}", event.getClass().getSimpleName());
      return;
    }

    String topic = routing.topic;
    String key = routing.keyExtractor.apply(event);

    Object payload = buildPayload(event);

    sendMessage(topic, key, payload);
  }

  private Object buildPayload(ApplicationEvent event) {
    if (event instanceof VoucherRedeemedEvent e) {
      return new VoucherRedeemedKafkaEvent(
          e.getVoucherId(),
          e.getCode(),
          e.getCampaignId(),
          e.getCustomerId(),
          e.getValue(),
          e.getRedeemedAt());
    }
    if (event instanceof PointsAddedEvent e) {
      return new PointsAddedKafkaEvent(
          e.getLoyaltyAccountId().getId(), e.getPoints(), e.getReferenceId());
    }
    if (event instanceof PointsDeductedEvent e) {
      return new PointsDeductedKafkaEvent(
          e.getLoyaltyAccountId().getId(), e.getPoints(), e.getReferenceId());
    }
    if (event instanceof CampaignActivatedEvent e) {
      return new CampaignActivatedKafkaEvent(e.campaignId(), e.name(), e.activatedAt());
    }
    return event;
  }

  private void sendMessage(String topic, String key, Object payload) {
    kafkaTemplate
        .send(topic, key, payload)
        .whenComplete(
            (result, ex) -> {
              if (ex == null) {
                log.info(
                    "Event externalized to Kafka: topic={}, key={}, partition={}, offset={}",
                    topic,
                    key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
              } else {
                log.error("Failed to externalize event to Kafka: topic={}, key={}", topic, key, ex);
              }
            });
  }

  private record EventRouting(
      String topic, java.util.function.Function<ApplicationEvent, String> keyExtractor) {}
}
