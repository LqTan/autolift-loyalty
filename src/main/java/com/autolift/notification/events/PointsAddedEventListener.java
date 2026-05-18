package com.autolift.notification.events;

import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.loyalty.events.PointsAddedEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationPointsAddedListener")
public class PointsAddedEventListener implements ApplicationListener<PointsAddedEvent> {

  private static final Logger log = LoggerFactory.getLogger(PointsAddedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public PointsAddedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(PointsAddedEvent event) {
    log.info("Received PointsAddedEvent");

    CreateNotificationCommand command = new CreateNotificationCommand(
        NotificationEventType.POINTS_ADDED,
        NotificationChannel.IN_APP,
        null,
        "Points Added",
        "Points added to loyalty account",
        Map.of("event", "PointsAddedEvent"));

    createNotificationHandler.handle(command);
  }
}