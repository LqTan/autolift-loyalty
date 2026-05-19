package com.autolift.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException {

  public CustomerNotFoundException(String message) {
    super(message);
  }

  public static CustomerNotFoundException withId(String id) {
    return new CustomerNotFoundException("Customer not found with id: " + id);
  }
}
