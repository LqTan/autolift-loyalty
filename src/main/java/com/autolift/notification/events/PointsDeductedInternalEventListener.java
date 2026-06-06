package com.autolift.notification.events;

import com.autolift.infrastructure.kafka.events.PointsDeductedInternalEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationPointsDeductedInternalListener")
public class PointsDeductedInternalEventListener
    implements ApplicationListener<PointsDeductedInternalEvent> {

  private static final Logger log =
      LoggerFactory.getLogger(PointsDeductedInternalEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public PointsDeductedInternalEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(PointsDeductedInternalEvent event) {
    log.info(
        "Received PointsDeductedInternalEvent from Kafka: loyaltyAccountId={}, points={}",
        event.getLoyaltyAccountId(),
        event.getPoints());

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.POINTS_DEDUCTED,
            NotificationChannel.IN_APP,
            null,
            "Points Deducted (from Kafka)",
            "Points deducted from loyalty account: " + event.getPoints(),
            Map.of(
                "loyaltyAccountId", event.getLoyaltyAccountId().toString(),
                "points", event.getPoints().toString(),
                "referenceId", event.getReferenceId(),
                "source", "kafka"));

    createNotificationHandler.handle(command);
  }
}
