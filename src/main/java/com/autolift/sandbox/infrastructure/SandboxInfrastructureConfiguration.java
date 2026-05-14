package com.autolift.sandbox.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.autolift.sandbox.domain.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class SandboxInfrastructureConfiguration {
}