package com.autolift.customer.application.command;

import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.events.CustomerCreatedEvent;
import com.autolift.customer.events.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CreateCustomerCommandHandler {

  private final CustomerRepository repository;
  private final DomainEventPublisher eventPublisher;

  public CreateCustomerCommandHandler(CustomerRepository repository, DomainEventPublisher eventPublisher) {
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
    eventPublisher.publish(new CustomerCreatedEvent(
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