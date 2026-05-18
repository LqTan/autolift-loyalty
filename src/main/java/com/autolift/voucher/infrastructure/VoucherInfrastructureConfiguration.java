package com.autolift.voucher.infrastructure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.autolift.voucher.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.autolift.voucher.infrastructure.persistence.entity")
@EnableTransactionManagement
public class VoucherInfrastructureConfiguration {}