package com.autolift.campaign.domain.repository;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {

  Campaign save(Campaign campaign);

  Optional<Campaign> findById(CampaignId id);

  List<Campaign> findAll();

  List<Campaign> findByStatus(CampaignStatus status);

  void deleteById(CampaignId id);
}
