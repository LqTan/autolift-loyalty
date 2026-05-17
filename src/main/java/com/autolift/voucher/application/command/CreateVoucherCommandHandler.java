package com.autolift.voucher.application.command;

import com.autolift.voucher.application.command.CreateVoucherResult;
import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import com.autolift.voucher.events.DomainEventPublisher;
import com.autolift.voucher.events.VoucherCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CreateVoucherCommandHandler {

  private final VoucherRepository repository;
  private final DomainEventPublisher eventPublisher;

  public CreateVoucherCommandHandler(VoucherRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public CreateVoucherResult handle(CreateVoucherCommand command) {
    Voucher voucher = Voucher.create(
        command.code(),
        command.campaignId(),
        command.type(),
        command.value(),
        command.minOrderAmount(),
        command.maxUsage(),
        command.validFrom(),
        command.validUntil());
    repository.save(voucher);
    eventPublisher.publish(new VoucherCreatedEvent(
        voucher.getId().getId().toString(),
        voucher.getCode(),
        voucher.getCampaignId()));
    return new CreateVoucherResult(
        voucher.getId().getId().toString(),
        voucher.getCode(),
        voucher.getCampaignId(),
        voucher.getType(),
        voucher.getValue(),
        voucher.getMinOrderAmount(),
        voucher.getMaxUsage(),
        voucher.getUsedCount(),
        voucher.getStatus(),
        voucher.getValidFrom(),
        voucher.getValidUntil());
  }
}