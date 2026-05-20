package com.autolift.campaign.application.query;

import com.autolift.campaign.infrastructure.persistence.readmodel.CampaignReadRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public Page<CampaignView> handle(GetAllCampaignsQuery query, Pageable pageable) {
    return readRepository.findAll(pageable);
  }
}
