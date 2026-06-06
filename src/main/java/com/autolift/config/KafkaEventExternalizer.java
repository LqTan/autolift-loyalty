package com.autolift.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventExternalizer implements ApplicationListener<ApplicationEvent> {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventExternalizer.class);

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final Map<Class<? extends ApplicationEvent>, EventRouting> routingRules = new HashMap<>();

  public KafkaEventExternalizer(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
    registerRoutingRules();
  }

  private void registerRoutingRules() {
    routingRules.put(
        com.autolift.voucher.events.VoucherRedeemedEvent.class,
        new EventRouting(KafkaConfig.VOUCHER_REDEEMED_TOPIC, e -> {
          var event = (com.autolift.voucher.events.VoucherRedeemedEvent) e;
          return event.getVoucherId();
        }));

    routingRules.put(
        com.autolift.loyalty.events.PointsAddedEvent.class,
        new EventRouting(KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC, e -> {
          var event = (com.autolift.loyalty.events.PointsAddedEvent) e;
          return event.getLoyaltyAccountId().getId().toString();
        }));

    routingRules.put(
        com.autolift.loyalty.events.PointsDeductedEvent.class,
        new EventRouting(KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC, e -> {
          var event = (com.autolift.loyalty.events.PointsDeductedEvent) e;
          return event.getLoyaltyAccountId().getId().toString();
        }));

    routingRules.put(
        com.autolift.campaign.events.CampaignActivatedEvent.class,
        new EventRouting(KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC, e -> {
          var event = (com.autolift.campaign.events.CampaignActivatedEvent) e;
          return event.campaignId();
        }));
  }

  @Override
  public void onApplicationEvent(ApplicationEvent event) {
    EventRouting routing = routingRules.get(event.getClass());
    if (routing == null) {
      log.debug("No Kafka routing configured for event: {}", event.getClass().getSimpleName());
      return;
    }

    String topic = routing.topic;
    String key = routing.keyExtractor.apply(event);

    Map<String, Object> payload = new HashMap<>();
    payload.put("eventType", event.getClass().getSimpleName());
    payload.put("timestamp", event.getTimestamp());
    payload.put("source", event.getSource().getClass().getSimpleName());
    payload.put("payload", event);

    sendMessage(topic, key, payload);
  }

  private void sendMessage(String topic, String key, Map<String, Object> payload) {
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

  private record EventRouting(String topic, java.util.function.Function<ApplicationEvent, String> keyExtractor) {}
}