package com.autolift.customer.application.command;

import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.events.CustomerCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CreateCustomerCommandHandler {

  private final CustomerRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public CreateCustomerCommandHandler(CustomerRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  @org.springframework.transaction.annotation.Transactional
  public CreateCustomerResult handle(CreateCustomerCommand command) {
    Customer customer = Customer.create(
        command.name(),
        command.email(),
        command.phone(),
        command.segment());
    repository.save(customer);
    eventPublisher.publishEvent(new CustomerCreatedEvent(
        customer.getId().getId().toString(),
        customer.getName(),
        customer.getEmail()));
    return new CreateCustomerResult(
        customer.getId().getId().toString(),
        customer.getName(),
        customer.getEmail(),
        customer.getPhone(),
        customer.getSegment().name(),
        customer.getStatus().name());
  }
}