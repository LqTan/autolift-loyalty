package com.autolift.notification.infrastructure;

import com.autolift.notification.infrastructure.persistence.repository.NotificationJpaRepository;
import com.autolift.notification.infrastructure.persistence.repository.NotificationRepositoryAdapter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.autolift.notification.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.autolift.notification.infrastructure.persistence.entity")
public class NotificationInfrastructureConfiguration {

  @Bean
  public NotificationRepositoryAdapter notificationRepositoryAdapter(
      NotificationJpaRepository jpaRepository) {
    return new NotificationRepositoryAdapter(jpaRepository);
  }
}