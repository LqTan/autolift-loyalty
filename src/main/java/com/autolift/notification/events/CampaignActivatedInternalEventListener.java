package com.autolift.notification.events;

import com.autolift.kafka.infrastructure.kafka.events.CampaignActivatedInternalEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationCampaignActivatedInternalListener")
public class CampaignActivatedInternalEventListener
    implements ApplicationListener<CampaignActivatedInternalEvent> {

  private static final Logger log =
      LoggerFactory.getLogger(CampaignActivatedInternalEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public CampaignActivatedInternalEventListener(
      CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(CampaignActivatedInternalEvent event) {
    log.info(
        "Received CampaignActivatedInternalEvent from Kafka: campaignId={}", event.getCampaignId());

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.CAMPAIGN_ACTIVATED,
            NotificationChannel.IN_APP,
            null,
            "Campaign Activated (from Kafka): " + event.getName(),
            "Campaign " + event.getCampaignId() + " has been activated",
            Map.of(
                "campaignId", event.getCampaignId(),
                "name", event.getName(),
                "activatedAt", event.getActivatedAt().toString(),
                "source", "kafka"));

    createNotificationHandler.handle(command);
  }
}
