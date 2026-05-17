package com.autolift.voucher.application.query;

import com.autolift.voucher.api.query.VoucherResponse;
import com.autolift.voucher.domain.exception.VoucherNotFoundException;
import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import org.springframework.stereotype.Component;

@Component
public class GetVoucherQueryHandler {

  private final VoucherRepository repository;

  public GetVoucherQueryHandler(VoucherRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public VoucherResponse handle(GetVoucherQuery query) {
    Voucher voucher = repository.findByCode(query.code())
        .orElseThrow(() -> VoucherNotFoundException.withCode(query.code()));
    return toResponse(voucher);
  }

  private VoucherResponse toResponse(Voucher voucher) {
    return new VoucherResponse(
        voucher.getId().getId().toString(),
        voucher.getCode(),
        voucher.getCampaignId(),
        voucher.getType().name(),
        voucher.getValue(),
        voucher.getMinOrderAmount(),
        voucher.getMaxUsage(),
        voucher.getUsedCount(),
        voucher.getStatus().name(),
        voucher.getValidFrom(),
        voucher.getValidUntil());
  }
}