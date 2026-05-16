package com.autolift.promotion.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.autolift.promotion.application.command.CreatePromotionCommand;
import com.autolift.promotion.application.command.CreatePromotionCommandHandler;
import com.autolift.promotion.application.command.CreatePromotionResult;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.infrastructure.persistence.entity.PromotionJpaEntity;
import com.autolift.promotion.infrastructure.persistence.mapper.PromotionPersistenceMapper;
import com.autolift.promotion.infrastructure.persistence.repository.PromotionJpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Transactional
class PromotionRepositoryAdapterIntegrationTest {

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
    registry.add("spring.flyway.locations", () -> "classpath:db/migration/promotion");
    registry.add("spring.jpa.hibernate.default_schema", () -> "promotion");
  }

  @Autowired private PromotionJpaRepository jpaRepository;

  @Autowired private PromotionPersistenceMapper mapper;

  @Autowired private PromotionRepository repository;

  @Autowired private CreatePromotionCommandHandler createHandler;

  @Test
  void shouldSaveAndRetrievePromotion() {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Integration Test Promotion",
            "Testing integration",
            com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    CreatePromotionResult result = createHandler.handle(command);

    assertThat(result.id()).isNotNull();
    assertThat(result.name()).isEqualTo("Integration Test Promotion");
    assertThat(result.status()).isEqualTo("DRAFT");
    assertThat(result.promotionType())
        .isEqualTo(com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE);

    PromotionJpaEntity saved = jpaRepository.findById(UUID.fromString(result.id())).orElseThrow();
    assertThat(saved.getName()).isEqualTo("Integration Test Promotion");
  }

  @Test
  void shouldFindAllPromotions() {
    createHandler.handle(
        new CreatePromotionCommand(
            "Promo 1",
            null,
            com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE,
            new BigDecimal("10"),
            null,
            null,
            null,
            null));
    createHandler.handle(
        new CreatePromotionCommand(
            "Promo 2",
            null,
            com.autolift.promotion.domain.valueobject.PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            null,
            null,
            null,
            null));
    createHandler.handle(
        new CreatePromotionCommand(
            "Promo 3",
            null,
            com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE,
            new BigDecimal("15"),
            null,
            null,
            null,
            null));

    var promotions = repository.findAll();

    assertThat(promotions).hasSize(3);
  }

  @Test
  void shouldFindPromotionsByType() {
    createHandler.handle(
        new CreatePromotionCommand(
            "Percentage Promo",
            null,
            com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE,
            new BigDecimal("25"),
            null,
            null,
            null,
            null));

    createHandler.handle(
        new CreatePromotionCommand(
            "Fixed Amount Promo",
            null,
            com.autolift.promotion.domain.valueobject.PromotionType.FIXED_AMOUNT,
            new BigDecimal("75000"),
            null,
            null,
            null,
            null));

    var percentagePromotions =
        repository.findAll().stream()
            .filter(
                p ->
                    p.getPromotionType()
                        == com.autolift.promotion.domain.valueobject.PromotionType.PERCENTAGE)
            .toList();
    var fixedAmountPromotions =
        repository.findAll().stream()
            .filter(
                p ->
                    p.getPromotionType()
                        == com.autolift.promotion.domain.valueobject.PromotionType.FIXED_AMOUNT)
            .toList();

    assertThat(percentagePromotions).hasSize(1);
    assertThat(fixedAmountPromotions).hasSize(1);
  }
}
