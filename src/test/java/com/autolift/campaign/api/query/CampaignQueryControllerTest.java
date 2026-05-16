package com.autolift.campaign.api.query;

import com.autolift.campaign.application.query.CampaignView;
import com.autolift.campaign.application.query.GetAllCampaignsQuery;
import com.autolift.campaign.application.query.GetAllCampaignsQueryHandler;
import com.autolift.campaign.application.query.GetCampaignQuery;
import com.autolift.campaign.application.query.GetCampaignQueryHandler;
import com.autolift.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignQueryController.class)
@Import({
    GetCampaignQueryHandler.class,
    GetAllCampaignsQueryHandler.class,
    SecurityConfig.class
})
class CampaignQueryControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GetCampaignQueryHandler getHandler;

    @MockBean
    private GetAllCampaignsQueryHandler getAllHandler;

    @Test
    void shouldGetCampaignById() throws Exception {
        String campaignId = "550e8400-e29b-41d4-a716-446655440000";
        CampaignView view = new CampaignView(
            campaignId,
            "Summer Sale",
            "Summer promotion",
            "ACTIVE",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND"
        );

        when(getHandler.handle(any(GetCampaignQuery.class))).thenReturn(Optional.of(view));

        mvc.perform(get("/api/campaigns/{id}", campaignId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(campaignId))
            .andExpect(jsonPath("$.name").value("Summer Sale"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn404WhenCampaignNotFound() throws Exception {
        String campaignId = "non-existent-id";

        when(getHandler.handle(any(GetCampaignQuery.class))).thenReturn(Optional.empty());

        mvc.perform(get("/api/campaigns/{id}", campaignId))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllCampaigns() throws Exception {
        List<CampaignView> campaigns = List.of(
            new CampaignView(
                "550e8400-e29b-41d4-a716-446655440000",
                "Campaign 1",
                "Description 1",
                "DRAFT",
                Instant.now(),
                Instant.now(),
                new BigDecimal("1000000"),
                "VND"
            ),
            new CampaignView(
                "550e8400-e29b-41d4-a716-446655440001",
                "Campaign 2",
                "Description 2",
                "ACTIVE",
                Instant.now(),
                Instant.now(),
                new BigDecimal("2000000"),
                "VND"
            )
        );

        when(getAllHandler.handle(any(GetAllCampaignsQuery.class))).thenReturn(campaigns);

        mvc.perform(get("/api/campaigns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Campaign 1"))
            .andExpect(jsonPath("$[1].name").value("Campaign 2"));
    }

    @Test
    void shouldReturnEmptyListWhenNoCampaigns() throws Exception {
        when(getAllHandler.handle(any(GetAllCampaignsQuery.class))).thenReturn(List.of());

        mvc.perform(get("/api/campaigns"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}