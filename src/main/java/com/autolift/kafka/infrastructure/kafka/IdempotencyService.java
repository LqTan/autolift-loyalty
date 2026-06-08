package com.autolift.kafka.infrastructure.kafka;

import com.autolift.kafka.infrastructure.persistence.entity.ProcessedEventJpaEntity;
import com.autolift.kafka.infrastructure.persistence.repository.ProcessedEventJpaRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdempotencyService {

  private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

  private final ProcessedEventJpaRepository processedEventRepository;

  public IdempotencyService(ProcessedEventJpaRepository processedEventRepository) {
    this.processedEventRepository = processedEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean isAlreadyProcessed(String eventId) {
    return processedEventRepository.existsByEventId(eventId);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markAsProcessed(String eventId, String eventType) {
    if (!processedEventRepository.existsByEventId(eventId)) {
      ProcessedEventJpaEntity entity = ProcessedEventJpaEntity.create(eventId, eventType);
      processedEventRepository.save(entity);
      log.debug("Marked event as processed: eventId={}, eventType={}", eventId, eventType);
    }
  }

  public String buildVoucherRedeemedEventId(String voucherId, String redeemedAt) {
    return "VoucherRedeemed:" + voucherId + ":" + redeemedAt;
  }

  public String buildPointsAddedEventId(UUID loyaltyAccountId, String referenceId) {
    return "PointsAdded:" + loyaltyAccountId + ":" + referenceId;
  }

  public String buildPointsDeductedEventId(UUID loyaltyAccountId, String referenceId) {
    return "PointsDeducted:" + loyaltyAccountId + ":" + referenceId;
  }

  public String buildCampaignActivatedEventId(String campaignId) {
    return "CampaignActivated:" + campaignId;
  }
}
