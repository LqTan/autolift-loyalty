package com.autolift.explainability.infrastructure;

import com.autolift.explainability.infrastructure.persistence.repository.GpRuleJpaRepository;
import com.autolift.explainability.infrastructure.persistence.repository.GpRuleRepositoryAdapter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.autolift.explainability.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.autolift.explainability.infrastructure.persistence.entity")
public class ExplainabilityInfrastructureConfiguration {

  @Bean
  public GpRuleRepositoryAdapter gpRuleRepositoryAdapter(GpRuleJpaRepository jpaRepository) {
    return new GpRuleRepositoryAdapter(jpaRepository);
  }
}
