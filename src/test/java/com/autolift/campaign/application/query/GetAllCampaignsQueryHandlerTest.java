package com.autolift.campaign.application.query;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllCampaignsQueryHandlerTest {

    @Mock
    private CampaignReadRepository readRepository;

    private GetAllCampaignsQueryHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetAllCampaignsQueryHandler(readRepository);
    }

    @Test
    void shouldReturnAllCampaigns() {
        List<CampaignView> campaigns = List.of(
            new CampaignView(
                UUID.randomUUID().toString(),
                "Campaign 1",
                "Description 1",
                "DRAFT",
                Instant.now(),
                Instant.now(),
                new BigDecimal("1000000"),
                "VND"
            ),
            new CampaignView(
                UUID.randomUUID().toString(),
                "Campaign 2",
                "Description 2",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                new BigDecimal("2000000"),
                "VND"
            )
        );

        when(readRepository.findAll()).thenReturn(campaigns);

        GetAllCampaignsQuery query = new GetAllCampaignsQuery();
        List<CampaignView> result = handler.handle(query);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Campaign 1");
        assertThat(result.get(1).name()).isEqualTo("Campaign 2");
    }

    @Test
    void shouldReturnEmptyListWhenNoCampaigns() {
        when(readRepository.findAll()).thenReturn(List.of());

        GetAllCampaignsQuery query = new GetAllCampaignsQuery();
        List<CampaignView> result = handler.handle(query);

        assertThat(result).isEmpty();
    }
}