package com.autolift.notification.events;

import com.autolift.notification.application.command.CreateNotificationCommand;
import com.autolift.notification.application.command.CreateNotificationHandler;
import com.autolift.notification.domain.valueobject.NotificationChannel;
import com.autolift.notification.domain.valueobject.NotificationEventType;
import com.autolift.voucher.events.VoucherRedeemedEvent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component("notificationVoucherRedeemedListener")
public class VoucherRedeemedEventListener implements ApplicationListener<VoucherRedeemedEvent> {

  private static final Logger log = LoggerFactory.getLogger(VoucherRedeemedEventListener.class);

  private final CreateNotificationHandler createNotificationHandler;

  public VoucherRedeemedEventListener(CreateNotificationHandler createNotificationHandler) {
    this.createNotificationHandler = createNotificationHandler;
  }

  @Override
  public void onApplicationEvent(VoucherRedeemedEvent event) {
    log.info("Received VoucherRedeemedEvent: voucherId={}, customerId={}",
        event.getVoucherId(), event.getCustomerId());

    CreateNotificationCommand command = new CreateNotificationCommand(
        NotificationEventType.VOUCHER_REDEEMED,
        NotificationChannel.IN_APP,
        event.getCustomerId(),
        "Voucher Redeemed",
        "Voucher " + event.getVoucherId() + " has been redeemed",
        Map.of(
            "voucherId", event.getVoucherId(),
            "customerId", event.getCustomerId(),
            "redeemedAt", event.getRedeemedAt().toString()));

    createNotificationHandler.handle(command);
  }
}