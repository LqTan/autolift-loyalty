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
  private final IdempotencyService idempotencyService;

  public KafkaEventConsumer(
      ApplicationEventPublisher eventPublisher, IdempotencyService idempotencyService) {
    this.eventPublisher = eventPublisher;
    this.idempotencyService = idempotencyService;
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

    String eventId =
        idempotencyService.buildVoucherRedeemedEventId(
            event.voucherId(), event.redeemedAt().toString());
    if (idempotencyService.isAlreadyProcessed(eventId)) {
      log.warn("Duplicate VoucherRedeemed event skipped: eventId={}", eventId);
      return;
    }

    VoucherRedeemedInternalEvent internalEvent =
        new VoucherRedeemedInternalEvent(
            event.voucherId(),
            event.code(),
            event.campaignId(),
            event.customerId(),
            event.value(),
            event.redeemedAt());
    eventPublisher.publishEvent(internalEvent);
    idempotencyService.markAsProcessed(eventId, "VoucherRedeemed");
    log.info("VoucherRedeemed event processed and marked: eventId={}", eventId);
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

    String eventId =
        idempotencyService.buildPointsAddedEventId(event.loyaltyAccountId(), event.referenceId());
    if (idempotencyService.isAlreadyProcessed(eventId)) {
      log.warn("Duplicate PointsAdded event skipped: eventId={}", eventId);
      return;
    }

    PointsAddedInternalEvent internalEvent =
        new PointsAddedInternalEvent(event.loyaltyAccountId(), event.points(), event.referenceId());
    eventPublisher.publishEvent(internalEvent);
    idempotencyService.markAsProcessed(eventId, "PointsAdded");
    log.info("PointsAdded event processed and marked: eventId={}", eventId);
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

    String eventId =
        idempotencyService.buildPointsDeductedEventId(
            event.loyaltyAccountId(), event.referenceId());
    if (idempotencyService.isAlreadyProcessed(eventId)) {
      log.warn("Duplicate PointsDeducted event skipped: eventId={}", eventId);
      return;
    }

    PointsDeductedInternalEvent internalEvent =
        new PointsDeductedInternalEvent(
            event.loyaltyAccountId(), event.points(), event.referenceId());
    eventPublisher.publishEvent(internalEvent);
    idempotencyService.markAsProcessed(eventId, "PointsDeducted");
    log.info("PointsDeducted event processed and marked: eventId={}", eventId);
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

    String eventId = idempotencyService.buildCampaignActivatedEventId(event.campaignId());
    if (idempotencyService.isAlreadyProcessed(eventId)) {
      log.warn("Duplicate CampaignActivated event skipped: eventId={}", eventId);
      return;
    }

    CampaignActivatedInternalEvent internalEvent =
        new CampaignActivatedInternalEvent(event.campaignId(), event.name(), event.activatedAt());
    eventPublisher.publishEvent(internalEvent);
    idempotencyService.markAsProcessed(eventId, "CampaignActivated");
    log.info("CampaignActivated event processed and marked: eventId={}", eventId);
  }
}
