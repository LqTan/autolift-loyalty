package com.autolift.ml.infrastructure;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.autolift.ml.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.autolift.ml.infrastructure.persistence.entity")
public class MlInfrastructureConfiguration {}
