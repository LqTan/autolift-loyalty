package com.autolift.voucher.infrastructure.persistence.repository;

import com.autolift.voucher.domain.model.Voucher;
import com.autolift.voucher.domain.repository.VoucherRepository;
import com.autolift.voucher.domain.valueobject.VoucherId;
import com.autolift.voucher.infrastructure.persistence.mapper.VoucherPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class VoucherRepositoryAdapter implements VoucherRepository {

  private final VoucherJpaRepository jpaRepository;
  private final VoucherPersistenceMapper mapper;

  public VoucherRepositoryAdapter(
      VoucherJpaRepository jpaRepository, VoucherPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Voucher save(Voucher voucher) {
    var entity = mapper.toEntity(voucher);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Voucher> findById(VoucherId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public Optional<Voucher> findByCode(String code) {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .filter(v -> v.getCode().equals(code))
        .findFirst();
  }

  @Override
  public List<Voucher> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Page<Voucher> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(mapper::toDomain);
  }

  @Override
  public List<Voucher> findByCampaignId(String campaignId) {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .filter(v -> v.getCampaignId().equals(campaignId))
        .toList();
  }

  @Override
  public void deleteById(VoucherId id) {
    jpaRepository.deleteById(id.getId());
  }
}
