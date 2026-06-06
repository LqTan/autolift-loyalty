package com.autolift.config;

import com.autolift.infrastructure.kafka.dto.CampaignActivatedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.PointsAddedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.PointsDeductedKafkaEvent;
import com.autolift.infrastructure.kafka.dto.VoucherRedeemedKafkaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
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
      @Payload VoucherRedeemedKafkaEvent event,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "VoucherRedeemed event received: topic={}, partition={}, offset={}, event={}",
        topic,
        partition,
        offset,
        event);
  }

  @KafkaListener(
      topics = KafkaConfig.LOYALTY_POINTS_ADDED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onPointsAdded(
      @Payload PointsAddedKafkaEvent event,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "PointsAdded event received: topic={}, partition={}, offset={}, event={}",
        topic,
        partition,
        offset,
        event);
  }

  @KafkaListener(
      topics = KafkaConfig.LOYALTY_POINTS_DEDUCTED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onPointsDeducted(
      @Payload PointsDeductedKafkaEvent event,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "PointsDeducted event received: topic={}, partition={}, offset={}, event={}",
        topic,
        partition,
        offset,
        event);
  }

  @KafkaListener(
      topics = KafkaConfig.CAMPAIGN_ACTIVATED_TOPIC,
      groupId = "autolift-group",
      containerFactory = "kafkaListenerContainerFactory")
  public void onCampaignActivated(
      @Payload CampaignActivatedKafkaEvent event,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {
    log.info(
        "CampaignActivated event received: topic={}, partition={}, offset={}, event={}",
        topic,
        partition,
        offset,
        event);
  }
}
