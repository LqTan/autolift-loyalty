package com.autolift.notification.application.command;

import com.autolift.notification.domain.model.Notification;
import com.autolift.notification.domain.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateNotificationHandler {

  private static final Logger log = LoggerFactory.getLogger(CreateNotificationHandler.class);

  private final NotificationRepository repository;

  public CreateNotificationHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public Notification handle(CreateNotificationCommand command) {
    Notification notification =
        Notification.create(
            command.eventType(),
            command.channel(),
            command.recipient(),
            command.subject(),
            command.body(),
            command.payload());

    Notification saved = repository.save(notification);
    log.info(
        "Notification created: id={}, eventType={}, channel={}",
        saved.getId().getId(),
        saved.getEventType(),
        saved.getChannel());
    return saved;
  }
}
