package com.autolift.customer.api.query;

import com.autolift.customer.application.query.GetAllCustomersQuery;
import com.autolift.customer.application.query.GetAllCustomersQueryHandler;
import com.autolift.customer.application.query.GetCustomerQuery;
import com.autolift.customer.application.query.GetCustomerQueryHandler;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@Import({GetCustomerQueryHandler.class, GetAllCustomersQueryHandler.class})
public class CustomerQueryController {

  private final GetCustomerQueryHandler getHandler;
  private final GetAllCustomersQueryHandler getAllHandler;

  public CustomerQueryController(
      GetCustomerQueryHandler getHandler, GetAllCustomersQueryHandler getAllHandler) {
    this.getHandler = getHandler;
    this.getAllHandler = getAllHandler;
  }

  @GetMapping
  public List<CustomerResponse> findAll() {
    return getAllHandler.handle(new GetAllCustomersQuery());
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<CustomerResponse> findById(@PathVariable String customerId) {
    try {
      return ResponseEntity.ok(getHandler.handle(new GetCustomerQuery(customerId)));
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
