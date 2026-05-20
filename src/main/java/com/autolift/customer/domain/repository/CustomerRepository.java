package com.autolift.customer.domain.repository;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.valueobject.CustomerId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerRepository {

  Customer save(Customer customer);

  Optional<Customer> findById(CustomerId id);

  List<Customer> findAll();

  Page<Customer> findAll(Pageable pageable);

  List<Customer> findBySegment(String segment);

  void deleteById(CustomerId id);
}
