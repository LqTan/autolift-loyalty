package com.autolift.promotion.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.autolift.promotion.infrastructure.persistence.repository")
@EnableTransactionManagement
public class PromotionInfrastructureConfiguration {}
