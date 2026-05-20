package com.autolift.customer.application.query;

import com.autolift.customer.api.query.CustomerResponse;
import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GetAllCustomersQueryHandler {

  private final CustomerRepository repository;

  public GetAllCustomersQueryHandler(CustomerRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public Page<CustomerResponse> handle(GetAllCustomersQuery query, Pageable pageable) {
    return repository.findAll(pageable).map(this::toResponse);
  }

  private CustomerResponse toResponse(Customer customer) {
    return new CustomerResponse(
        customer.getId().getId().toString(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getSegment().name(),
        customer.getStatus().name());
  }
}
