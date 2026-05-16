package com.autolift.campaign.application.query;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetAllCampaignsQueryHandler {

  private final CampaignReadRepository readRepository;

  public GetAllCampaignsQueryHandler(CampaignReadRepository readRepository) {
    this.readRepository = readRepository;
  }

  public List<CampaignView> handle(GetAllCampaignsQuery query) {
    return readRepository.findAll();
  }
}
