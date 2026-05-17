package com.autolift.customer.application.command;

import com.autolift.customer.domain.exception.CustomerNotFoundException;
import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class SuspendCustomerCommandHandler {

  private final CustomerRepository repository;

  public SuspendCustomerCommandHandler(CustomerRepository repository) {
    this.repository = repository;
  }

  @org.springframework.transaction.annotation.Transactional
  public void handle(SuspendCustomerCommand command) {
    Customer customer = repository.findById(CustomerId.of(command.customerId()))
        .orElseThrow(() -> CustomerNotFoundException.withId(command.customerId()));
    customer.suspend();
    repository.save(customer);
  }
}