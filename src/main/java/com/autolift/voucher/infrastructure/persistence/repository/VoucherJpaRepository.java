package com.autolift.voucher.infrastructure.persistence.repository;

import com.autolift.voucher.infrastructure.persistence.entity.VoucherJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherJpaRepository extends JpaRepository<VoucherJpaEntity, UUID> {}
