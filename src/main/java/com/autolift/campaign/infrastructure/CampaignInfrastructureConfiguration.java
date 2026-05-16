package com.autolift.campaign.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.autolift.campaign.infrastructure.persistence.repository")
@EnableTransactionManagement
public class CampaignInfrastructureConfiguration {}
