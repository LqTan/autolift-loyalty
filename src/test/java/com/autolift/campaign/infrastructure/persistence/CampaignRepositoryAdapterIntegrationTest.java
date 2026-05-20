package com.autolift.campaign.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.autolift.campaign.application.command.ActivateCampaignCommandHandler;
import com.autolift.campaign.application.command.CampaignCreatedResult;
import com.autolift.campaign.application.command.CreateCampaignCommand;
import com.autolift.campaign.application.command.CreateCampaignCommandHandler;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.infrastructure.persistence.entity.CampaignJpaEntity;
import com.autolift.campaign.infrastructure.persistence.mapper.CampaignPersistenceMapper;
import com.autolift.campaign.infrastructure.persistence.repository.CampaignJpaRepository;
import com.autolift.config.TestCacheConfig;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Transactional
@Import(TestCacheConfig.class)
class CampaignRepositoryAdapterIntegrationTest {

  @SuppressWarnings("resource")
  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:16-alpine")
          .withDatabaseName("autolift")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.locations", () -> "classpath:db/migration/campaign");
    registry.add("spring.jpa.hibernate.default_schema", () -> "campaign");
  }

  @Autowired private CampaignJpaRepository jpaRepository;

  @Autowired private CampaignPersistenceMapper mapper;

  @Autowired private CampaignRepository repository;

  @Autowired private CreateCampaignCommandHandler createHandler;

  @Autowired private ActivateCampaignCommandHandler activateHandler;

  @Test
  void shouldSaveAndRetrieveCampaign() {
    CreateCampaignCommand command =
        new CreateCampaignCommand(
            "Integration Test Campaign",
            "Testing integration",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND");

    CampaignCreatedResult result = createHandler.handle(command);

    assertThat(result.id()).isNotNull();
    assertThat(result.name()).isEqualTo("Integration Test Campaign");
    assertThat(result.status()).isEqualTo("DRAFT");

    CampaignJpaEntity saved = jpaRepository.findById(UUID.fromString(result.id())).orElseThrow();
    assertThat(saved.getName()).isEqualTo("Integration Test Campaign");
  }

  @Test
  void shouldFindAllCampaigns() {
    createHandler.handle(
        new CreateCampaignCommand("Campaign 1", null, null, null, new BigDecimal("1000"), "VND"));
    createHandler.handle(
        new CreateCampaignCommand("Campaign 2", null, null, null, new BigDecimal("2000"), "VND"));
    createHandler.handle(
        new CreateCampaignCommand("Campaign 3", null, null, null, new BigDecimal("3000"), "VND"));

    var campaigns = repository.findAll();

    assertThat(campaigns).hasSize(3);
  }
}
