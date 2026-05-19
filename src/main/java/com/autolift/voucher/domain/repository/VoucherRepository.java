package com.autolift.voucher.domain.repository;

import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.valueobject.VoucherId;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository {

  Voucher save(Voucher voucher);

  Optional<Voucher> findById(VoucherId id);

  Optional<Voucher> findByCode(String code);

  List<Voucher> findAll();

  List<Voucher> findByCampaignId(String campaignId);

  void deleteById(VoucherId id);
}
