package com.autolift.campaign.infrastructure.persistence;

import com.autolift.campaign.application.command.ActivateCampaignCommand;
import com.autolift.campaign.application.command.ActivateCampaignCommandHandler;
import com.autolift.campaign.application.command.CampaignCreatedResult;
import com.autolift.campaign.application.command.CreateCampaignCommand;
import com.autolift.campaign.application.command.CreateCampaignCommandHandler;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.campaign.infrastructure.persistence.entity.CampaignJpaEntity;
import com.autolift.campaign.infrastructure.persistence.mapper.CampaignPersistenceMapper;
import com.autolift.campaign.infrastructure.persistence.repository.CampaignJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@Transactional
class CampaignRepositoryAdapterIntegrationTest {

    @SuppressWarnings("resource")
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
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

    @Autowired
    private CampaignJpaRepository jpaRepository;

    @Autowired
    private CampaignPersistenceMapper mapper;

    @Autowired
    private CampaignRepository repository;

    @Autowired
    private CreateCampaignCommandHandler createHandler;

    @Autowired
    private ActivateCampaignCommandHandler activateHandler;

    @Test
    void shouldSaveAndRetrieveCampaign() {
        CreateCampaignCommand command = new CreateCampaignCommand(
            "Integration Test Campaign",
            "Testing integration",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND"
        );

        CampaignCreatedResult result = createHandler.handle(command);

        assertThat(result.id()).isNotNull();
        assertThat(result.name()).isEqualTo("Integration Test Campaign");
        assertThat(result.status()).isEqualTo("DRAFT");

        CampaignJpaEntity saved = jpaRepository.findById(UUID.fromString(result.id())).orElseThrow();
        assertThat(saved.getName()).isEqualTo("Integration Test Campaign");
    }

    @Test
    void shouldActivateCampaignAndPublishEvent() {
        CreateCampaignCommand createCommand = new CreateCampaignCommand(
            "Campaign to Activate",
            null,
            null,
            null,
            new BigDecimal("1000000"),
            "VND"
        );
        CampaignCreatedResult created = createHandler.handle(createCommand);

        ActivateCampaignCommand activateCommand = new ActivateCampaignCommand(created.id());
        CampaignActivatedEvent event = activateHandler.handle(activateCommand);

        assertThat(event.campaignId()).isEqualTo(created.id());
        assertThat(event.name()).isEqualTo("Campaign to Activate");
        assertThat(event.activatedAt()).isNotNull();

        CampaignJpaEntity updated = jpaRepository.findById(UUID.fromString(created.id())).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldFindAllCampaigns() {
        createHandler.handle(new CreateCampaignCommand("Campaign 1", null, null, null, new BigDecimal("1000"), "VND"));
        createHandler.handle(new CreateCampaignCommand("Campaign 2", null, null, null, new BigDecimal("2000"), "VND"));
        createHandler.handle(new CreateCampaignCommand("Campaign 3", null, null, null, new BigDecimal("3000"), "VND"));

        var campaigns = repository.findAll();

        assertThat(campaigns).hasSize(3);
    }

    @Test
    void shouldFindCampaignsByStatus() {
        CreateCampaignCommand cmd1 = new CreateCampaignCommand("Draft 1", null, null, null, new BigDecimal("1000"), "VND");
        CampaignCreatedResult r1 = createHandler.handle(cmd1);

        CreateCampaignCommand cmd2 = new CreateCampaignCommand("Active 1", null, null, null, new BigDecimal("1000"), "VND");
        CampaignCreatedResult r2 = createHandler.handle(cmd2);
        activateHandler.handle(new ActivateCampaignCommand(r2.id()));

        var draftCampaigns = repository.findAll().stream()
            .filter(c -> c.getStatus() == com.autolift.campaign.domain.valueobject.CampaignStatus.DRAFT)
            .toList();
        var activeCampaigns = repository.findAll().stream()
            .filter(c -> c.getStatus() == com.autolift.campaign.domain.valueobject.CampaignStatus.ACTIVE)
            .toList();

        assertThat(draftCampaigns).hasSize(1);
        assertThat(activeCampaigns).hasSize(1);
    }
}