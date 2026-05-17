package com.autolift.voucher.application.command;

import com.autolift.voucher.domain.exception.VoucherNotFoundException;
import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import com.autolift.voucher.events.DomainEventPublisher;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import org.springframework.stereotype.Component;

@Component
public class RedeemVoucherCommandHandler {

  private final VoucherRepository repository;
  private final DomainEventPublisher eventPublisher;

  public RedeemVoucherCommandHandler(VoucherRepository repository, DomainEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(RedeemVoucherCommand command) {
    Voucher voucher = repository.findByCode(command.code())
        .orElseThrow(() -> VoucherNotFoundException.withCode(command.code()));
    voucher.redeem();
    repository.save(voucher);
    eventPublisher.publish(new VoucherRedeemedEvent(
        voucher.getId().getId().toString(),
        voucher.getCode(),
        voucher.getCampaignId(),
        voucher.getValue()));
  }
}