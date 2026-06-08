package com.autolift.kafka.application;

import com.autolift.kafka.infrastructure.persistence.entity.OutboxEventJpaEntity;
import com.autolift.kafka.infrastructure.persistence.entity.OutboxEventJpaEntity.Status;
import com.autolift.kafka.infrastructure.persistence.repository.OutboxEventJpaRepository;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxPublisher {

  private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
  private static final int BATCH_SIZE = 100;
  private static final int MAX_RETRIES = 3;

  private final OutboxEventJpaRepository outboxRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final AtomicBoolean isRunning = new AtomicBoolean(false);

  public OutboxPublisher(
      OutboxEventJpaRepository outboxRepository, KafkaTemplate<String, Object> kafkaTemplate) {
    this.outboxRepository = outboxRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Scheduled(fixedDelayString = "${kafka.outbox.poll-interval-ms:1000}")
  public void publishPendingEvents() {
    if (!isRunning.compareAndSet(false, true)) {
      return;
    }
    try {
      List<OutboxEventJpaEntity> pendingEvents =
          outboxRepository.findByStatusOrderByCreatedAtAscWithLimit(Status.PENDING, BATCH_SIZE);

      if (pendingEvents.isEmpty()) {
        return;
      }

      log.debug("Polling outbox: found {} pending events", pendingEvents.size());

      for (OutboxEventJpaEntity event : pendingEvents) {
        processEvent(event);
      }
    } finally {
      isRunning.set(false);
    }
  }

  private void processEvent(OutboxEventJpaEntity event) {
    try {
      event.markProcessing();
      outboxRepository.save(event);

      kafkaTemplate
          .send(event.getTopic(), event.getEventKey(), event.getPayload())
          .whenComplete(
              (result, ex) -> {
                if (ex == null) {
                  event.markCompleted();
                  log.info(
                      "Outbox event published: topic={}, key={}, partition={}, offset={}",
                      event.getTopic(),
                      event.getEventKey(),
                      result.getRecordMetadata().partition(),
                      result.getRecordMetadata().offset());
                } else {
                  handlePublishFailure(event, ex);
                }
                outboxRepository.save(event);
              });

    } catch (Exception e) {
      handlePublishFailure(event, e);
      outboxRepository.save(event);
    }
  }

  private void handlePublishFailure(OutboxEventJpaEntity event, Throwable ex) {
    if (event.canRetry(MAX_RETRIES)) {
      event.markFailed(ex.getMessage());
      log.warn(
          "Outbox event publish failed (will retry): topic={}, key={}, attempt={}, error={}",
          event.getTopic(),
          event.getEventKey(),
          event.getRetryCount(),
          ex.getMessage());
    } else {
      event.markFailed("Max retries exceeded: " + ex.getMessage());
      log.error(
          "Outbox event publish failed (max retries exceeded): topic={}, key={}, error={}",
          event.getTopic(),
          event.getEventKey(),
          ex.getMessage());
    }
  }
}
