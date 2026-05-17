package com.autolift.customer.events;

import java.time.Instant;
import lombok.Getter;

@Getter
public class CustomerCreatedEvent {

  private final String customerId;
  private final String name;
  private final String email;
  private final Instant createdAt;

  public CustomerCreatedEvent(String customerId, String name, String email) {
    this.customerId = customerId;
    this.name = name;
    this.email = email;
    this.createdAt = Instant.now();
  }
}