package com.autolift.notification.api.command;

import com.autolift.notification.application.command.MarkNotificationSentHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationStatusController {

  private final MarkNotificationSentHandler markSentHandler;

  public NotificationStatusController(MarkNotificationSentHandler markSentHandler) {
    this.markSentHandler = markSentHandler;
  }

  @PatchMapping("/{id}/sent")
  public ResponseEntity<Void> markSent(@PathVariable String id) {
    markSentHandler.handle(id);
    return ResponseEntity.noContent().build();
  }
}