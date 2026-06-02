package com.autolift.campaign.application.query;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class GetCampaignQueryHandler {

  private final CampaignReadRepository readRepository;

  public GetCampaignQueryHandler(CampaignReadRepository readRepository) {
    this.readRepository = readRepository;
  }

  @Cacheable(value = "campaigns", key = "#query.campaignId()")
  public Optional<CampaignView> handle(GetCampaignQuery query) {
    return readRepository.findById(query.campaignId());
  }
}
