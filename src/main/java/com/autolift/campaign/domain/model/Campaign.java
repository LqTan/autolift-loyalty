package com.autolift.campaign.domain.model;

import com.autolift.campaign.domain.exception.InvalidCampaignStateException;
import com.autolift.campaign.domain.valueobject.Budget;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Campaign {

    private final CampaignId id;
    private final String name;
    private final String description;
    private CampaignStatus status;
    private final Instant startDate;
    private final Instant endDate;
    private final Budget budget;
    private final Instant createdAt;
    private Instant updatedAt;

    protected Campaign() {
        this.id = null;
        this.name = null;
        this.description = null;
        this.status = null;
        this.startDate = null;
        this.endDate = null;
        this.budget = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    private Campaign(CampaignId id, String name, String description, CampaignStatus status,
                    Instant startDate, Instant endDate, Budget budget,
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Campaign create(String name, String description, Instant startDate,
                                  Instant endDate, Budget budget) {
        CampaignId id = CampaignId.random();
        Instant now = Instant.now();
        return new Campaign(id, name, description, CampaignStatus.DRAFT,
                startDate, endDate, budget, now, now);
    }

    public static Campaign of(CampaignId id, String name, String description, CampaignStatus status,
                             Instant startDate, Instant endDate, Budget budget,
                             Instant createdAt, Instant updatedAt) {
        return new Campaign(id, name, description, status, startDate, endDate, budget, createdAt, updatedAt);
    }

    public void activate() {
        if (this.status != CampaignStatus.DRAFT && this.status != CampaignStatus.PAUSED) {
            throw new InvalidCampaignStateException(
                "Cannot activate campaign with status: " + this.status);
        }
        this.status = CampaignStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void pause() {
        if (this.status != CampaignStatus.ACTIVE) {
            throw new InvalidCampaignStateException(
                "Cannot pause campaign with status: " + this.status);
        }
        this.status = CampaignStatus.PAUSED;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        if (this.status != CampaignStatus.ACTIVE) {
            throw new InvalidCampaignStateException(
                "Cannot complete campaign with status: " + this.status);
        }
        this.status = CampaignStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void expire() {
        if (this.status == CampaignStatus.COMPLETED || this.status == CampaignStatus.EXPIRED) {
            return;
        }
        this.status = CampaignStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }
}