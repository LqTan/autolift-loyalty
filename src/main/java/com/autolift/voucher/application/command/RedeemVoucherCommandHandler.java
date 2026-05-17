package com.autolift.voucher.application.command;

import com.autolift.voucher.domain.exception.VoucherNotFoundException;
import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RedeemVoucherCommandHandler {

  private final VoucherRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public RedeemVoucherCommandHandler(VoucherRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(RedeemVoucherCommand command) {
    Voucher voucher = repository.findByCode(command.code())
        .orElseThrow(() -> VoucherNotFoundException.withCode(command.code()));
    voucher.redeem();
    repository.save(voucher);
    eventPublisher.publishEvent(new VoucherRedeemedEvent(
        voucher.getId().getId().toString(),
        voucher.getCode(),
        voucher.getCampaignId(),
        command.customerId(),
        voucher.getValue()));
  }
}