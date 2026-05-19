package com.autolift.notification.api.query;

import com.autolift.notification.application.query.GetNotificationsHandler;
import com.autolift.notification.application.query.GetNotificationsQuery;
import com.autolift.notification.application.query.NotificationView;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationQueryController {

  private final GetNotificationsHandler getNotificationsHandler;

  public NotificationQueryController(GetNotificationsHandler getNotificationsHandler) {
    this.getNotificationsHandler = getNotificationsHandler;
  }

  @GetMapping
  public ResponseEntity<List<NotificationView>> getNotifications(
      @RequestParam(required = false) String eventType,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String recipient,
      @RequestParam(defaultValue = "50") int limit,
      @RequestParam(defaultValue = "0") int offset) {

    GetNotificationsQuery query =
        new GetNotificationsQuery(null, null, null, null, null, limit, offset);
    List<NotificationView> result = getNotificationsHandler.handle(query);
    return ResponseEntity.ok(result);
  }
}
