package com.autolift.notification.events;

import com.autolift.campaign.events.CampaignActivatedEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationCampaignListener")
public class CampaignActivatedEventListener implements ApplicationListener<CampaignActivatedEvent> {

  private static final Logger log = LoggerFactory.getLogger(CampaignActivatedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public CampaignActivatedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(CampaignActivatedEvent event) {
    log.info("Received CampaignActivatedEvent: campaignId={}", event.campaignId());

    CreateNotificationCommand command = new CreateNotificationCommand(
        NotificationEventType.CAMPAIGN_ACTIVATED,
        NotificationChannel.IN_APP,
        null,
        "Campaign Activated: " + event.name(),
        "Campaign " + event.campaignId() + " has been activated at " + event.activatedAt(),
        Map.of(
            "campaignId", event.campaignId(),
            "name", event.name(),
            "activatedAt", event.activatedAt().toString()));

    createNotificationHandler.handle(command);
  }
}