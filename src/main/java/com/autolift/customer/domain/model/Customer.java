package com.autolift.customer.domain.model;

import com.autolift.customer.domain.valueobject.CustomerId;
import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Customer {

  private final CustomerId id;
  private final String name;
  private final String email;
  private final String phone;
  private CustomerSegment segment;
  private CustomerStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  protected Customer() {
    this.id = null;
    this.name = null;
    this.email = null;
    this.phone = null;
    this.segment = null;
    this.status = null;
    this.createdAt = null;
    this.updatedAt = null;
  }

  private Customer(
      CustomerId id,
      String name,
      String email,
      String phone,
      CustomerSegment segment,
      CustomerStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.segment = segment;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Customer create(String name, String email, String phone, CustomerSegment segment) {
    CustomerId id = CustomerId.random();
    Instant now = Instant.now();
    return new Customer(id, name, email, phone, segment, CustomerStatus.ACTIVE, now, now);
  }

  public static Customer of(
      CustomerId id,
      String name,
      String email,
      String phone,
      CustomerSegment segment,
      CustomerStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new Customer(id, name, email, phone, segment, status, createdAt, updatedAt);
  }

  public void suspend() {
    if (this.status != CustomerStatus.ACTIVE) {
      throw new IllegalStateException("Cannot suspend customer with status: " + this.status);
    }
    this.status = CustomerStatus.SUSPENDED;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    if (this.status == CustomerStatus.SUSPENDED) {
      this.status = CustomerStatus.ACTIVE;
      this.updatedAt = Instant.now();
    }
  }

  public void deactivate() {
    if (this.status == CustomerStatus.ACTIVE) {
      this.status = CustomerStatus.INACTIVE;
      this.updatedAt = Instant.now();
    }
  }

  public void updateSegment(CustomerSegment newSegment) {
    this.segment = newSegment;
    this.updatedAt = Instant.now();
  }
}
