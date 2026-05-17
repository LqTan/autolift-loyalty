package com.autolift.customer.infrastructure.persistence.repository;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.domain.valueobject.CustomerId;
import com.autolift.customer.infrastructure.persistence.mapper.CustomerPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

  private final CustomerJpaRepository jpaRepository;
  private final CustomerPersistenceMapper mapper;

  public CustomerRepositoryAdapter(CustomerJpaRepository jpaRepository, CustomerPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Customer save(Customer customer) {
    var entity = mapper.toEntity(customer);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  public Optional<Customer> findById(CustomerId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public List<Customer> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Customer> findBySegment(String segment) {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .filter(c -> c.getSegment().name().equals(segment))
        .toList();
  }

  @Override
  public void deleteById(CustomerId id) {
    jpaRepository.deleteById(id.getId());
  }
}