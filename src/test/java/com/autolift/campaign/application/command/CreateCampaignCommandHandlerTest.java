package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCampaignCommandHandlerTest {

    @Mock
    private CampaignRepository repository;

    private CreateCampaignCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateCampaignCommandHandler(repository);
    }

    @Test
    void shouldCreateCampaignWithDraftStatus() {
        CreateCampaignCommand command = new CreateCampaignCommand(
            "Summer Sale",
            "Summer promotion",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND"
        );

        when(repository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CampaignCreatedResult result = handler.handle(command);

        ArgumentCaptor<Campaign> captor = ArgumentCaptor.forClass(Campaign.class);
        verify(repository).save(captor.capture());

        Campaign savedCampaign = captor.getValue();
        assertThat(savedCampaign.getName()).isEqualTo("Summer Sale");
        assertThat(savedCampaign.getDescription()).isEqualTo("Summer promotion");
        assertThat(savedCampaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
        assertThat(result.name()).isEqualTo("Summer Sale");
        assertThat(result.status()).isEqualTo("DRAFT");
    }

    @Test
    void shouldSaveCampaignWithAllFields() {
        CreateCampaignCommand command = new CreateCampaignCommand(
            "Winter Campaign",
            "Winter promotion",
            Instant.parse("2026-12-01T00:00:00Z"),
            Instant.parse("2026-12-31T23:59:59Z"),
            new BigDecimal("100000000"),
            "VND"
        );

        when(repository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CampaignCreatedResult result = handler.handle(command);

        assertThat(result.name()).isEqualTo("Winter Campaign");
        assertThat(result.description()).isEqualTo("Winter promotion");
        assertThat(result.budgetAmount()).isEqualByComparingTo(new BigDecimal("100000000"));
    }
}