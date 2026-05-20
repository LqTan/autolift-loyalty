package com.autolift.voucher.application.query;

import com.autolift.voucher.api.query.VoucherResponse;
import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GetAllVouchersQueryHandler {

  private final VoucherRepository repository;

  public GetAllVouchersQueryHandler(VoucherRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<VoucherResponse> handle(GetAllVouchersQuery query) {
    return repository.findAll().stream().map(this::toResponse).toList();
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public Page<VoucherResponse> handle(GetAllVouchersQuery query, Pageable pageable) {
    return repository.findAll(pageable).map(this::toResponse);
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
