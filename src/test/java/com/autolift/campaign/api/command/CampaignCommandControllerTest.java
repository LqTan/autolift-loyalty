package com.autolift.campaign.api.command;

import com.autolift.campaign.application.command.ActivateCampaignCommand;
import com.autolift.campaign.application.command.ActivateCampaignCommandHandler;
import com.autolift.campaign.application.command.CampaignCreatedResult;
import com.autolift.campaign.application.command.CompleteCampaignCommand;
import com.autolift.campaign.application.command.CompleteCampaignCommandHandler;
import com.autolift.campaign.application.command.CreateCampaignCommand;
import com.autolift.campaign.application.command.CreateCampaignCommandHandler;
import com.autolift.campaign.application.command.PauseCampaignCommand;
import com.autolift.campaign.application.command.PauseCampaignCommandHandler;
import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CampaignCommandController.class)
@Import({
    CreateCampaignCommandHandler.class,
    ActivateCampaignCommandHandler.class,
    PauseCampaignCommandHandler.class,
    CompleteCampaignCommandHandler.class,
    SecurityConfig.class
})
class CampaignCommandControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreateCampaignCommandHandler createHandler;

    @MockBean
    private ActivateCampaignCommandHandler activateHandler;

    @MockBean
    private PauseCampaignCommandHandler pauseHandler;

    @MockBean
    private CompleteCampaignCommandHandler completeHandler;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void shouldCreateCampaign() throws Exception {
        CreateCampaignRequest request = new CreateCampaignRequest(
            "Summer Sale",
            "Summer promotion",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND"
        );

        CampaignCreatedResult result = new CampaignCreatedResult(
            "550e8400-e29b-41d4-a716-446655440000",
            "Summer Sale",
            "Summer promotion",
            "DRAFT",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            new BigDecimal("50000000"),
            "VND"
        );

        when(createHandler.handle(any(CreateCampaignCommand.class))).thenReturn(result);

        mvc.perform(post("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(jsonPath("$.name").value("Summer Sale"))
            .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldActivateCampaign() throws Exception {
        String campaignId = "550e8400-e29b-41d4-a716-446655440000";
        CampaignActivatedEvent event = new CampaignActivatedEvent(
            campaignId,
            "Summer Sale",
            Instant.now()
        );

        when(activateHandler.handle(any(ActivateCampaignCommand.class))).thenReturn(event);

        mvc.perform(post("/api/campaigns/{id}/activate", campaignId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.campaignId").value(campaignId))
            .andExpect(jsonPath("$.name").value("Summer Sale"));
    }

    @Test
    void shouldPauseCampaign() throws Exception {
        String campaignId = "550e8400-e29b-41d4-a716-446655440000";

        mvc.perform(post("/api/campaigns/{id}/pause", campaignId))
            .andExpect(status().isNoContent());

        verify(pauseHandler).handle(any(PauseCampaignCommand.class));
    }

    @Test
    void shouldCompleteCampaign() throws Exception {
        String campaignId = "550e8400-e29b-41d4-a716-446655440000";

        mvc.perform(post("/api/campaigns/{id}/complete", campaignId))
            .andExpect(status().isNoContent());

        verify(completeHandler).handle(any(CompleteCampaignCommand.class));
    }
}