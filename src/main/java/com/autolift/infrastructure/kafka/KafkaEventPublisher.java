package com.autolift.infrastructure.kafka;

import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.config.KafkaConfig;
import com.autolift.loyalty.events.PointsAddedEvent;
import com.autolift.loyalty.events.PointsDeductedEvent;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publishVoucherRedeemed(VoucherRedeemedEvent event) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("voucherId", event.getVoucherId());
    payload.put("code", event.getCode());
    payload.put("campaignId", event.getCampaignId());
    payload.put("customerId", event.getCustomerId());
    payload.put("value", event.getValue());
    payload.put("redeemedAt", event.getRedeemedAt());

    sendMessage(KafkaConfig.VOUCHER_REDEEMED_TOPIC, event.getVoucherId(), payload);
  }

  public void publishPointsAdded(PointsAddedEvent event) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("loyaltyAccountId", event.getLoyaltyAccountId().getId().toString());
    payload.put("points", event.getPoints());
    payload.put("referenceId", event.getReferenceId());

    sendMessage(
        KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC,
        event.getLoyaltyAccountId().getId().toString(),
        payload);
  }

  public void publishPointsDeducted(PointsDeductedEvent event) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("loyaltyAccountId", event.getLoyaltyAccountId().getId().toString());
    payload.put("points", event.getPoints());
    payload.put("referenceId", event.getReferenceId());

    sendMessage(
        KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC,
        event.getLoyaltyAccountId().getId().toString(),
        payload);
  }

  public void publishCampaignActivated(CampaignActivatedEvent event) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("campaignId", event.campaignId());
    payload.put("name", event.name());
    payload.put("activatedAt", event.activatedAt());

    sendMessage(KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC, event.campaignId(), payload);
  }

  private void sendMessage(String topic, String key, Map<String, Object> payload) {
    kafkaTemplate
        .send(topic, key, payload)
        .whenComplete(
            (result, ex) -> {
              if (ex == null) {
                log.info(
                    "Event sent to Kafka: topic={}, key={}, partition={}, offset={}",
                    topic,
                    key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
              } else {
                log.error("Failed to send event to Kafka: topic={}, key={}", topic, key, ex);
              }
            });
  }
}
