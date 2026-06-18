package com.autolift.kafka.infrastructure.kafka;

import com.autolift.kafka.infrastructure.kafka.dto.CampaignActivatedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.PointsAddedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.PointsDeductedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.dto.VoucherRedeemedKafkaEvent;
import com.autolift.kafka.infrastructure.kafka.events.CampaignActivatedInternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.PointsAddedInternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.PointsDeductedInternalEvent;
import com.autolift.kafka.infrastructure.kafka.events.VoucherRedeemedInternalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventConsumer {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);

  private final ApplicationEventPublisher eventPublisher;

  public KafkaEventConsumer(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

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
        "VoucherRedeemed Kafka event received: topic={}, partition={}, offset={}",
        topic,
        partition,
        offset);

    VoucherRedeemedInternalEvent internalEvent =
        new VoucherRedeemedInternalEvent(
            event.voucherId(),
            event.code(),
            event.campaignId(),
            event.customerId(),
            event.value(),
            event.redeemedAt());
    eventPublisher.publishEvent(internalEvent);
    log.info("VoucherRedeemed event processed");
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
        "PointsAdded Kafka event received: topic={}, partition={}, offset={}",
        topic,
        partition,
        offset);

    PointsAddedInternalEvent internalEvent =
        new PointsAddedInternalEvent(event.loyaltyAccountId(), event.points(), event.referenceId());
    eventPublisher.publishEvent(internalEvent);
    log.info("PointsAdded event processed");
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
        "PointsDeducted Kafka event received: topic={}, partition={}, offset={}",
        topic,
        partition,
        offset);

    PointsDeductedInternalEvent internalEvent =
        new PointsDeductedInternalEvent(
            event.loyaltyAccountId(), event.points(), event.referenceId());
    eventPublisher.publishEvent(internalEvent);
    log.info("PointsDeducted event processed");
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
        "CampaignActivated Kafka event received: topic={}, partition={}, offset={}",
        topic,
        partition,
        offset);

    CampaignActivatedInternalEvent internalEvent =
        new CampaignActivatedInternalEvent(event.campaignId(), event.name(), event.activatedAt());
    eventPublisher.publishEvent(internalEvent);
    log.info("CampaignActivated event processed");
  }
}
