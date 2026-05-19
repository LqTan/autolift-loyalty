package com.autolift.voucher.api.query;

import com.autolift.voucher.application.query.GetAllVouchersQuery;
import com.autolift.voucher.application.query.GetAllVouchersQueryHandler;
import com.autolift.voucher.application.query.GetVoucherQuery;
import com.autolift.voucher.application.query.GetVoucherQueryHandler;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vouchers")
@Import({GetVoucherQueryHandler.class, GetAllVouchersQueryHandler.class})
public class VoucherQueryController {

  private final GetVoucherQueryHandler getHandler;
  private final GetAllVouchersQueryHandler getAllHandler;

  public VoucherQueryController(
      GetVoucherQueryHandler getHandler, GetAllVouchersQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping
  public List<VoucherResponse> findAll() {
    return getAllHandler.handle(new GetAllVouchersQuery());
  }

  @GetMapping("/{code}")
  public ResponseEntity<VoucherResponse> findByCode(@PathVariable String code) {
    try {
      return ResponseEntity.ok(getHandler.handle(new GetVoucherQuery(code)));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
