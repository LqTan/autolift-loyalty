package com.autolift.notification.events;

import com.autolift.infrastructure.kafka.events.VoucherRedeemedInternalEvent;
import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationVoucherRedeemedInternalListener")
public class VoucherRedeemedInternalEventListener
    implements ApplicationListener<VoucherRedeemedInternalEvent> {

  private static final Logger log =
      LoggerFactory.getLogger(VoucherRedeemedInternalEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public VoucherRedeemedInternalEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(VoucherRedeemedInternalEvent event) {
    log.info(
        "Received VoucherRedeemedInternalEvent from Kafka: voucherId={}, customerId={}",
        event.getVoucherId(),
        event.getCustomerId());

    CreateNotificationCommand command =
        new CreateNotificationCommand(
            NotificationEventType.VOUCHER_REDEEMED,
            NotificationChannel.IN_APP,
            event.getCustomerId(),
            "Voucher Redeemed (from Kafka)",
            "Voucher " + event.getVoucherId() + " has been redeemed",
            Map.of(
                "voucherId", event.getVoucherId(),
                "customerId", event.getCustomerId(),
                "redeemedAt", event.getRedeemedAt().toString(),
                "source", "kafka"));

    createNotificationHandler.handle(command);
  }
}
