package com.autolift.campaign.domain.model;

import com.autolift.campaign.domain.exception.InvalidCampaignStateException;
import com.autolift.campaign.domain.valueobject.Budget;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignTest {

    @Test
    void shouldCreateCampaignWithDraftStatus() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            "Summer promotion",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            Budget.of(new BigDecimal("50000000"))
        );

        assertThat(campaign.getId()).isNotNull();
        assertThat(campaign.getName()).isEqualTo("Summer Sale");
        assertThat(campaign.getDescription()).isEqualTo("Summer promotion");
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
        assertThat(campaign.getStartDate()).isNotNull();
        assertThat(campaign.getEndDate()).isNotNull();
        assertThat(campaign.getBudget()).isNotNull();
    }

    @Test
    void shouldActivateDraftCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            Budget.of(new BigDecimal("50000000"))
        );

        campaign.activate();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void shouldActivatePausedCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            Budget.of(new BigDecimal("50000000"))
        );
        campaign.activate();
        campaign.pause();

        campaign.activate();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }

    @Test
    void shouldRejectActivationOfActiveCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();

        assertThatThrownBy(campaign::activate)
            .isInstanceOf(InvalidCampaignStateException.class)
            .hasMessageContaining("Cannot activate campaign with status: ACTIVE");
    }

    @Test
    void shouldRejectActivationOfCompletedCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();
        campaign.complete();

        assertThatThrownBy(campaign::activate)
            .isInstanceOf(InvalidCampaignStateException.class)
            .hasMessageContaining("Cannot activate campaign with status: COMPLETED");
    }

    @Test
    void shouldPauseActiveCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();

        campaign.pause();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.PAUSED);
    }

    @Test
    void shouldRejectPauseOfNonActiveCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(campaign::pause)
            .isInstanceOf(InvalidCampaignStateException.class)
            .hasMessageContaining("Cannot pause campaign with status: DRAFT");
    }

    @Test
    void shouldCompleteActiveCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();

        campaign.complete();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.COMPLETED);
    }

    @Test
    void shouldRejectCompleteOfNonActiveCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(campaign::complete)
            .isInstanceOf(InvalidCampaignStateException.class)
            .hasMessageContaining("Cannot complete campaign with status: DRAFT");
    }

    @Test
    void shouldExpireCampaign() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();

        campaign.expire();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.EXPIRED);
    }

    @Test
    void shouldNotChangeStatusWhenExpireAlreadyExpiredOrCompleted() {
        Campaign campaign = Campaign.create(
            "Summer Sale",
            null,
            null,
            null,
            null
        );
        campaign.activate();
        campaign.complete();

        campaign.expire();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.COMPLETED);
    }

    @Test
    void shouldCreateCampaignWithoutOptionalFields() {
        Campaign campaign = Campaign.create(
            "Minimal Campaign",
            null,
            null,
            null,
            null
        );

        assertThat(campaign.getId()).isNotNull();
        assertThat(campaign.getName()).isEqualTo("Minimal Campaign");
        assertThat(campaign.getDescription()).isNull();
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }
}