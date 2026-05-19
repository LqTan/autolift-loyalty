package com.autolift.notification.application.query;

import com.autolift.notification.domain.repository.NotificationRepository;
import com.autolift.notification.domain.valueobject.NotificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationStatsHandler {

  private final NotificationRepository repository;

  public GetNotificationStatsHandler(NotificationRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public NotificationStatsView handle() {
    long pending = repository.countByStatus(NotificationStatus.PENDING);
    long sent = repository.countByStatus(NotificationStatus.SENT);
    long failed = repository.countByStatus(NotificationStatus.FAILED);
    return new NotificationStatsView(pending, sent, failed);
  }

  public record NotificationStatsView(long pending, long sent, long failed) {}
}
