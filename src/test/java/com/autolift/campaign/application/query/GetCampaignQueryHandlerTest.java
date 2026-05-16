package com.autolift.campaign.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCampaignQueryHandlerTest {

  @Mock private CampaignReadRepository readRepository;

  private GetCampaignQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetCampaignQueryHandler(readRepository);
  }

  @Test
  void shouldReturnCampaignWhenFound() {
    String campaignId = UUID.randomUUID().toString();
    CampaignView view =
        new CampaignView(
            campaignId,
            "Summer Sale",
            "Promotion",
            "DRAFT",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND");

    when(readRepository.findById(campaignId)).thenReturn(Optional.of(view));

    GetCampaignQuery query = new GetCampaignQuery(campaignId);
    Optional<CampaignView> result = handler.handle(query);

    assertThat(result).isPresent();
    assertThat(result.get().name()).isEqualTo("Summer Sale");
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    String campaignId = UUID.randomUUID().toString();

    when(readRepository.findById(campaignId)).thenReturn(Optional.empty());

    GetCampaignQuery query = new GetCampaignQuery(campaignId);
    Optional<CampaignView> result = handler.handle(query);

    assertThat(result).isEmpty();
  }
}
