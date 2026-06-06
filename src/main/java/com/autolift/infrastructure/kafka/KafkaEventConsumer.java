package com.autolift.infrastructure.kafka;

import com.autolift.config.KafkaConfig;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventConsumer {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

  @KafkaListener(
      topics = KafkaConfig.VOUCHER_REDEEMED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onVoucherRedeemed(
      @Payload Map<String, Object> payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "Kafka event received: topic={}, partition={}, offset={}, payload={}",
        topic,
        partition,
        offset,
        payload);
  }

  @KafkaListener(
      topics = KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onPointsAdded(
      @Payload Map<String, Object> payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "Kafka event received: topic={}, partition={}, offset={}, payload={}",
        topic,
        partition,
        offset,
        payload);
  }

  @KafkaListener(
      topics = KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onPointsDeducted(
      @Payload Map<String, Object> payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "Kafka event received: topic={}, partition={}, offset={}, payload={}",
        topic,
        partition,
        offset,
        payload);
  }

  @KafkaListener(
      topics = KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onCampaignActivated(
      @Payload Map<String, Object> payload,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "Kafka event received: topic={}, partition={}, offset={}, payload={}",
        topic,
        partition,
        offset,
        payload);
  }
}
