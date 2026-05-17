package com.autolift.voucher.infrastructure.persistence.repository;

import com.autolift.voucher.infrastructure.persistence.entity.VoucherJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherJpaRepository extends JpaRepository<VoucherJpaEntity, UUID> {}