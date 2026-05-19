package com.autolift.notification.application.query;

import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import java.time.Instant;

public record GetNotificationsQuery(
    NotificationEventType eventType,
    NotificationStatus status,
    String recipient,
    Instant startDate,
    Instant endDate,
    int limit,
    int offset) {
  public GetNotificationsQuery {
    if (limit <= 0) limit = 50;
    if (limit > 200) limit = 200;
    if (offset < 0) offset = 0;
  }

  public static GetNotificationsQuery all() {
    return new GetNotificationsQuery(null, null, null, null, null, 50, 0);
  }

  public static GetNotificationsQuery byEventType(NotificationEventType eventType) {
    return new GetNotificationsQuery(eventType, null, null, null, null, 50, 0);
  }

  public static GetNotificationsQuery byStatus(NotificationStatus status) {
    return new GetNotificationsQuery(null, status, null, null, null, 50, 0);
  }

  public static GetNotificationsQuery byRecipient(String recipient) {
    return new GetNotificationsQuery(null, null, recipient, null, null, 50, 0);
  }

  public static GetNotificationsQuery byDateRange(Instant start, Instant end) {
    return new GetNotificationsQuery(null, null, null, start, end, 50, 0);
  }
}
