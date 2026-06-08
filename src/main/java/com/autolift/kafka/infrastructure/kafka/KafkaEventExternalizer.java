package com.autolift.kafka.infrastructure.kafka;

import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.kafka.infrastructure.kafka.dto.CampaignActivatedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.PointsAddedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.PointsDeductedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.VoucherRedeemedKafkaEvent;
import com.autolift.kafka.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.autolift.kafka.infrastructure.persistence.repository.OutboxEventJpaRepository;
import com.autolift.loyalty.events.PointsAddedEvent;
import com.autolift.loyalty.events.PointsDeductedEvent;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class KafkaEventExternalizer {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventExternalizer.class);

  private final OutboxEventJpaRepository outboxRepository;
  private final ObjectMapper objectMapper;
  private final Map<Class<? extends ApplicationEvent>, EventRouting> routingRules = new HashMap<>();

  public KafkaEventExternalizer(
      OutboxEventJpaRepository outboxRepository, ObjectMapper objectMapper) {
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
    registerRoutingRules();
  }

  private void registerRoutingRules() {
    routingRules.put(
        VoucherRedeemedEvent.class,
        new EventRouting(
            KafkaConfig.VOUCHER_REDEEMED_TOPIC,
            "VoucherRedeemed",
            e -> {
              var event = (VoucherRedeemedEvent) e;
              return event.getVoucherId();
            }));

    routingRules.put(
        PointsAddedEvent.class,
        new EventRouting(
            KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC,
            "PointsAdded",
            e -> {
              var event = (PointsAddedEvent) e;
              return event.getLoyaltyAccountId().getId().toString();
            }));

    routingRules.put(
        PointsDeductedEvent.class,
        new EventRouting(
            KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC,
            "PointsDeducted",
            e -> {
              var event = (PointsDeductedEvent) e;
              return event.getLoyaltyAccountId().getId().toString();
            }));

    routingRules.put(
        CampaignActivatedEvent.class,
        new EventRouting(
            KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC,
            "CampaignActivated",
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

    saveToOutbox(routing.eventType, topic, key, payload);
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

  private void saveToOutbox(String eventType, String topic, String key, Object payload) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(payload);
      OutboxEventJpaEntity outboxEvent =
          OutboxEventJpaEntity.create(eventType, eventType, topic, key, jsonPayload);
      outboxRepository.save(outboxEvent);
      log.info("Event saved to outbox: type={}, topic={}, key={}", eventType, topic, key);
    } catch (JsonProcessingException e) {
      log.error(
          "Failed to serialize event payload: type={}, topic={}, key={}", eventType, topic, key, e);
    }
  }

  private record EventRouting(
      String topic,
      String eventType,
      java.util.function.Function<ApplicationEvent, String> keyExtractor) {}
}
