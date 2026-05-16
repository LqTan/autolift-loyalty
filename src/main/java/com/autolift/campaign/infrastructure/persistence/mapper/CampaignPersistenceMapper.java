package com.autolift.campaign.infrastructure.persistence.mapper;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.valueobject.Budget;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import com.autolift.campaign.infrastructure.persistence.entity.CampaignJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CampaignPersistenceMapper {

    public Campaign toDomain(CampaignJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Campaign.of(
            CampaignId.of(entity.getId()),
            entity.getName(),
            entity.getDescription(),
            CampaignStatus.valueOf(entity.getStatus()),
            entity.getStartDate(),
            entity.getEndDate(),
            Budget.of(entity.getBudgetAmount(), entity.getBudgetCurrency()),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public CampaignJpaEntity toEntity(Campaign domain) {
        if (domain == null) {
            return null;
        }
        return new CampaignJpaEntity(
            domain.getId().getId(),
            domain.getName(),
            domain.getDescription(),
            domain.getStatus().name(),
            domain.getStartDate(),
            domain.getEndDate(),
            domain.getBudget().getAmount(),
            domain.getBudget().getCurrency(),
            domain.getCreatedAt(),
            domain.getUpdatedAt()
        );
    }
}