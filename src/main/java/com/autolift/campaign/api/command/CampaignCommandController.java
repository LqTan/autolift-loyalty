package com.autolift.campaign.api.command;

import com.autolift.campaign.application.command.ActivateCampaignCommand;
import com.autolift.campaign.application.command.ActivateCampaignCommandHandler;
import com.autolift.campaign.application.command.CampaignCreatedResult;
import com.autolift.campaign.application.command.CompleteCampaignCommand;
import com.autolift.campaign.application.command.CompleteCampaignCommandHandler;
import com.autolift.campaign.application.command.CreateCampaignCommand;
import com.autolift.campaign.application.command.CreateCampaignCommandHandler;
import com.autolift.campaign.application.command.PauseCampaignCommand;
import com.autolift.campaign.application.command.PauseCampaignCommandHandler;
import com.autolift.campaign.events.CampaignActivatedEvent;
import java.net.URI;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
@Import({
  CreateCampaignCommandHandler.class,
  ActivateCampaignCommandHandler.class,
  PauseCampaignCommandHandler.class,
  CompleteCampaignCommandHandler.class
})
public class CampaignCommandController {

  private final CreateCampaignCommandHandler createHandler;
  private final ActivateCampaignCommandHandler activateHandler;
  private final PauseCampaignCommandHandler pauseHandler;
  private final CompleteCampaignCommandHandler completeHandler;

  public CampaignCommandController(
      CreateCampaignCommandHandler createHandler,
      ActivateCampaignCommandHandler activateHandler,
      PauseCampaignCommandHandler pauseHandler,
      CompleteCampaignCommandHandler completeHandler) {
    this.createHandler = createHandler;
    this.activateHandler = activateHandler;
    this.pauseHandler = pauseHandler;
    this.completeHandler = completeHandler;
  }

  @PostMapping
  public ResponseEntity<CampaignCreatedResult> create(@RequestBody CreateCampaignRequest request) {
    CreateCampaignCommand command =
        new CreateCampaignCommand(
            request.name(),
            request.description(),
            request.startDate(),
            request.endDate(),
            request.budgetAmount(),
            request.budgetCurrency());
    CampaignCreatedResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/campaigns/" + result.id())).body(result);
  }

  @PostMapping("/{id}/activate")
  public ResponseEntity<CampaignActivatedResponse> activate(@PathVariable String id) {
    ActivateCampaignCommand command = new ActivateCampaignCommand(id);
    CampaignActivatedEvent event = activateHandler.handle(command);
    CampaignActivatedResponse response =
        new CampaignActivatedResponse(event.campaignId(), event.name(), event.activatedAt());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{id}/pause")
  public ResponseEntity<Void> pause(@PathVariable String id) {
    PauseCampaignCommand command = new PauseCampaignCommand(id);
    pauseHandler.handle(command);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<Void> complete(@PathVariable String id) {
    CompleteCampaignCommand command = new CompleteCampaignCommand(id);
    completeHandler.handle(command);
    return ResponseEntity.noContent().build();
  }
}
