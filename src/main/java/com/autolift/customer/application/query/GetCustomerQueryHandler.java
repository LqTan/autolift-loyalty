package com.autolift.customer.application.query;

import com.autolift.customer.api.query.CustomerResponse;
import com.autolift.customer.domain.exception.CustomerNotFoundException;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class GetCustomerQueryHandler {

  private final CustomerRepository repository;

  public GetCustomerQueryHandler(CustomerRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public CustomerResponse handle(GetCustomerQuery query) {
    return repository.findById(CustomerId.of(query.customerId()))
        .map(this::toResponse)
        .orElseThrow(() -> CustomerNotFoundException.withId(query.customerId()));
  }

  private CustomerResponse toResponse(com.autolift.customer.domain.model.Customer customer) {
    return new CustomerResponse(
        customer.getId().getId().toString(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getSegment().name(),
        customer.getStatus().name());
  }
}