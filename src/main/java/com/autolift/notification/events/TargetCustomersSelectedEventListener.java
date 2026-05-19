package com.autolift.notification.events;

import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.targeting.events.TargetCustomersSelectedEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationTargetCustomersListener")
public class TargetCustomersSelectedEventListener
    implements ApplicationListener<TargetCustomersSelectedEvent> {

  private static final Logger log =
      LoggerFactory.getLogger(TargetCustomersSelectedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public TargetCustomersSelectedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(TargetCustomersSelectedEvent event) {
    log.info("Received TargetCustomersSelectedEvent");

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.TARGET_CUSTOMERS_SELECTED,
            NotificationChannel.IN_APP,
            null,
            "Target Customers Selected",
            "Target customers selected event received",
            Map.of("event", "TargetCustomersSelectedEvent"));

    createNotificationHandler.handle(command);
  }
}
