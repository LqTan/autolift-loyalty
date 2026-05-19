package com.autolift.notification.events;

import com.autolift.loyalty.events.PointsDeductedEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationPointsDeductedListener")
public class PointsDeductedEventListener implements ApplicationListener<PointsDeductedEvent> {

  private static final Logger log = LoggerFactory.getLogger(PointsDeductedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public PointsDeductedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(PointsDeductedEvent event) {
    log.info("Received PointsDeductedEvent");

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.POINTS_DEDUCTED,
            NotificationChannel.IN_APP,
            null,
            "Points Deducted",
            "Points deducted from loyalty account",
            Map.of("event", "PointsDeductedEvent"));

    createNotificationHandler.handle(command);
  }
}
