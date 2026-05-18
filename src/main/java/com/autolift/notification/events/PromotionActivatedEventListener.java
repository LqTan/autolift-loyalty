package com.autolift.notification.events;

import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.promotion.events.PromotionActivatedEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationPromotionListener")
public class PromotionActivatedEventListener implements ApplicationListener<PromotionActivatedEvent> {

  private static final Logger log = LoggerFactory.getLogger(PromotionActivatedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public PromotionActivatedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(PromotionActivatedEvent event) {
    log.info("Received PromotionActivatedEvent: promotionId={}", event.getPromotionId());

    CreateNotificationCommand command = new CreateNotificationCommand(
        NotificationEventType.PROMOTION_ACTIVATED,
        NotificationChannel.IN_APP,
        null,
        "Promotion Activated: " + event.getName(),
        "Promotion " + event.getPromotionId() + " has been activated",
        Map.of(
            "promotionId", event.getPromotionId(),
            "name", event.getName()));

    createNotificationHandler.handle(command);
  }
}