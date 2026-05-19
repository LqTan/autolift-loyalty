package com.autolift.customer.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import org.junit.jupiter.api.Test;

class CustomerTest {

  @Test
  void shouldCreateCustomer() {
    Customer customer =
        Customer.create("John Doe", "john@example.com", "0987654321", CustomerSegment.NORMAL);

    assertNotNull(customer.getId());
    assertEquals("John Doe", customer.getName());
    assertEquals("john@example.com", customer.getEmail());
    assertEquals("0987654321", customer.getPhone());
    assertEquals(CustomerSegment.NORMAL, customer.getSegment());
    assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
  }

  @Test
  void shouldCreateCustomerWithVipSegment() {
    Customer customer =
        Customer.create("VIP Customer", "vip@example.com", "0987654321", CustomerSegment.VIP);

    assertEquals(CustomerSegment.VIP, customer.getSegment());
  }

  @Test
  void shouldSuspendCustomer() {
    Customer customer =
        Customer.create("John Doe", "john@example.com", "0987654321", CustomerSegment.NORMAL);

    customer.suspend();

    assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());
  }

  @Test
  void shouldActivateCustomer() {
    Customer customer =
        Customer.create("John Doe", "john@example.com", "0987654321", CustomerSegment.NORMAL);
    customer.suspend();

    customer.activate();

    assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
  }

  @Test
  void shouldNotSuspendAlreadySuspendedCustomer() {
    Customer customer =
        Customer.create("John Doe", "john@example.com", "0987654321", CustomerSegment.NORMAL);
    customer.suspend();

    customer.activate();
    customer.suspend();

    assertEquals(CustomerStatus.SUSPENDED, customer.getStatus());
  }
}
