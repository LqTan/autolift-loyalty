package com.autolift.customer.domain.repository;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.valueobject.CustomerId;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(CustomerId id);

  List<Customer> findAll();

  List<Customer> findBySegment(String segment);

  void deleteById(CustomerId id);
}