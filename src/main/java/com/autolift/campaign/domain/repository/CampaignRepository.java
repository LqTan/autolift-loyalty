package com.autolift.campaign.domain.repository;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignRepository {

  Campaign save(Campaign campaign);

  Optional<Campaign> findById(CampaignId id);

  List<Campaign> findAll();

  Page<Campaign> findAll(Pageable pageable);

  List<Campaign> findByStatus(CampaignStatus status);

  void deleteById(CampaignId id);
}
