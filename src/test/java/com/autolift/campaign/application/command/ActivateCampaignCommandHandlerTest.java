package com.autolift.campaign.application.command;

import com.autolift.campaign.domain.exception.CampaignNotFoundException;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.campaign.events.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivateCampaignCommandHandlerTest {

    @Mock
    private CampaignRepository repository;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ActivateCampaignCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ActivateCampaignCommandHandler(repository, eventPublisher);
    }

    @Test
    void shouldActivateDraftCampaign() {
        String campaignId = UUID.randomUUID().toString();
        Campaign campaign = Campaign.create("Test", null, null, null, null);

        when(repository.findById(any(CampaignId.class))).thenReturn(Optional.of(campaign));
        when(repository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActivateCampaignCommand command = new ActivateCampaignCommand(campaignId);
        CampaignActivatedEvent event = handler.handle(command);

        assertThat(event.campaignId()).isEqualTo(campaign.getId().getId().toString());
        assertThat(event.name()).isEqualTo("Test");
        verify(eventPublisher).publish(any(CampaignActivatedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenCampaignNotFound() {
        String campaignId = UUID.randomUUID().toString();

        when(repository.findById(any(CampaignId.class))).thenReturn(Optional.empty());

        ActivateCampaignCommand command = new ActivateCampaignCommand(campaignId);

        assertThatThrownBy(() -> handler.handle(command))
            .isInstanceOf(CampaignNotFoundException.class)
            .hasMessageContaining(campaignId);
    }

    @Test
    void shouldPublishCampaignActivatedEvent() {
        String campaignId = UUID.randomUUID().toString();
        Campaign campaign = Campaign.create("Test", null, null, null, null);

        when(repository.findById(any(CampaignId.class))).thenReturn(Optional.of(campaign));
        when(repository.save(any(Campaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActivateCampaignCommand command = new ActivateCampaignCommand(campaignId);
        handler.handle(command);

        verify(eventPublisher).publish(any(CampaignActivatedEvent.class));
    }
}