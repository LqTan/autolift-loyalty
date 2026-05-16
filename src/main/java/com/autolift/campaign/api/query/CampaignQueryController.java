package com.autolift.campaign.api.query;

import com.autolift.campaign.application.query.CampaignView;
import com.autolift.campaign.application.query.GetAllCampaignsQuery;
import com.autolift.campaign.application.query.GetAllCampaignsQueryHandler;
import com.autolift.campaign.application.query.GetCampaignQuery;
import com.autolift.campaign.application.query.GetCampaignQueryHandler;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
@Import({GetCampaignQueryHandler.class, GetAllCampaignsQueryHandler.class})
public class CampaignQueryController {

  private final GetCampaignQueryHandler getHandler;
  private final GetAllCampaignsQueryHandler getAllHandler;

  public CampaignQueryController(
      GetCampaignQueryHandler getHandler, GetAllCampaignsQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping
  public List<CampaignResponse> findAll() {
    return getAllHandler.handle(new GetAllCampaignsQuery()).stream().map(this::toResponse).toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<CampaignResponse> findById(@PathVariable String id) {
    return getHandler
        .handle(new GetCampaignQuery(id))
        .map(c -> ResponseEntity.ok(toResponse(c)))
        .orElse(ResponseEntity.notFound().build());
  }

  private CampaignResponse toResponse(CampaignView view) {
    return new CampaignResponse(
        view.id(),
        view.name(),
        view.description(),
        view.status(),
        view.startDate(),
        view.endDate(),
        view.budgetAmount(),
        view.budgetCurrency());
  }
}
