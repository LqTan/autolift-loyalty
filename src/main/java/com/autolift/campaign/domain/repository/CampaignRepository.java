package com.autolift.campaign.domain.repository;

import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.valueobject.CampaignId;
import com.autolift.campaign.domain.valueobject.CampaignStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface CampaignRepository {

  @CacheEvict(value = "campaigns", allEntries = true)
  Campaign save(Campaign campaign);

  @Cacheable(value = "campaigns", key = "#id.getId()")
  Optional<Campaign> findById(CampaignId id);

  @Cacheable(value = "campaigns", key = "'all'")
  List<Campaign> findAll();

  List<Campaign> findByStatus(CampaignStatus status);

  @CacheEvict(value = "campaigns", allEntries = true)
  void deleteById(CampaignId id);
}
