package com.autolift.notification.api.command;

import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationCommandController {

  private final CreateNotificationHandler createNotificationHandler;

  public NotificationCommandController(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @PostMapping
  public ResponseEntity<NotificationCommandResponse> create(@RequestBody CreateNotificationRequest request) {
    CreateNotificationCommand command = new CreateNotificationCommand(
        NotificationEventType.valueOf(request.eventType()),
        NotificationChannel.valueOf(request.channel()),
        request.recipient(),
        request.subject(),
        request.body(),
        request.payload());
    var result = createNotificationHandler.handle(command);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new NotificationCommandResponse(result.getId().getId().toString(), "PENDING"));
  }
}

record CreateNotificationRequest(
    String eventType,
    String channel,
    String recipient,
    String subject,
    String body,
    Map<String, Object> payload
) {}

record NotificationCommandResponse(String id, String status) {}