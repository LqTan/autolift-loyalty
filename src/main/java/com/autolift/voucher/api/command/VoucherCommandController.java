package com.autolift.voucher.api.command;

import com.autolift.voucher.application.command.CreateVoucherCommand;
import com.autolift.voucher.application.command.CreateVoucherCommandHandler;
import com.autolift.voucher.application.command.CreateVoucherResult;
import com.autolift.voucher.application.command.RedeemVoucherCommand;
import com.autolift.voucher.application.command.RedeemVoucherCommandHandler;
import java.net.URI;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vouchers")
@Import({CreateVoucherCommandHandler.class, RedeemVoucherCommandHandler.class})
public class VoucherCommandController {

  private final CreateVoucherCommandHandler createHandler;
  private final RedeemVoucherCommandHandler redeemHandler;

  public VoucherCommandController(
      CreateVoucherCommandHandler createHandler, RedeemVoucherCommandHandler redeemHandler) {
    this.createHandler = createHandler;
    this.redeemHandler = redeemHandler;
  }

  @PostMapping
  public ResponseEntity<CreateVoucherResult> create(@RequestBody CreateVoucherRequest request) {
    CreateVoucherCommand command =
        new CreateVoucherCommand(
            request.code(),
            request.campaignId(),
            request.voucherType(),
            request.value(),
            request.minOrderAmount(),
            request.maxUsage(),
            request.validFrom(),
            request.validUntil());
    CreateVoucherResult result = createHandler.handle(command);
    return ResponseEntity.created(URI.create("/api/vouchers/" + result.code())).body(result);
  }

  @PostMapping("/{code}/redeem")
  public ResponseEntity<Void> redeem(
      @PathVariable String code, @RequestBody RedeemVoucherRequest request) {
    redeemHandler.handle(new RedeemVoucherCommand(code, request.customerId()));
    return ResponseEntity.noContent().build();
  }
}
