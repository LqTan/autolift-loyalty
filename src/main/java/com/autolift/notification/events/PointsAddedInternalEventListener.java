package com.autolift.notification.events;

import com.autolift.kafka.infrastructure.kafka.events.PointsAddedInternalEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationPointsAddedInternalListener")
public class PointsAddedInternalEventListener
    implements ApplicationListener<PointsAddedInternalEvent> {

  private static final Logger log = LoggerFactory.getLogger(PointsAddedInternalEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public PointsAddedInternalEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(PointsAddedInternalEvent event) {
    log.info(
        "Received PointsAddedInternalEvent from Kafka: loyaltyAccountId={}, points={}",
        event.getLoyaltyAccountId(),
        event.getPoints());

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.POINTS_ADDED,
            NotificationChannel.IN_APP,
            null,
            "Points Added (from Kafka)",
            "Points added to loyalty account: " + event.getPoints(),
            Map.of(
                "loyaltyAccountId", event.getLoyaltyAccountId().toString(),
                "points", event.getPoints().toString(),
                "referenceId", event.getReferenceId(),
                "source", "kafka"));

    createNotificationHandler.handle(command);
  }
}
