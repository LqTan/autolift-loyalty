package com.autolift.targeting.infrastructure;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.autolift.targeting.infrastructure.persistence.repository")
@EnableTransactionManagement
@EntityScan(basePackages = "com.autolift.targeting.infrastructure.persistence.entity")
public class TargetingInfrastructureConfiguration {}
