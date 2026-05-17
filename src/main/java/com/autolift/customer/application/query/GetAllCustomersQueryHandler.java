package com.autolift.customer.application.query;

import com.autolift.customer.api.query.CustomerResponse;
import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllCustomersQueryHandler {

  private final CustomerRepository repository;

  public GetAllCustomersQueryHandler(CustomerRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public List<CustomerResponse> handle(GetAllCustomersQuery query) {
    return repository.findAll().stream()
        .map(this::toResponse)
        .toList();
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