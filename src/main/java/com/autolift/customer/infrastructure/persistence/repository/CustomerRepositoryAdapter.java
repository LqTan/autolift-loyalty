package com.autolift.customer.infrastructure.persistence.repository;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.repository.CustomerRepository;
import com.autolift.customer.domain.valueobject.CustomerId;
import com.autolift.customer.infrastructure.persistence.mapper.CustomerPersistenceMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

  private static final String CACHE_NAME = "customers";

  private final CustomerJpaRepository jpaRepository;
  private final CustomerPersistenceMapper mapper;

  public CustomerRepositoryAdapter(
      CustomerJpaRepository jpaRepository, CustomerPersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public Customer save(Customer customer) {
    var entity = mapper.toEntity(customer);
    entity = jpaRepository.save(entity);
    return mapper.toDomain(entity);
  }

  @Override
  @Cacheable(value = CACHE_NAME, key = "#id.getId().toString()")
  public Optional<Customer> findById(CustomerId id) {
    return jpaRepository.findById(id.getId()).map(mapper::toDomain);
  }

  @Override
  public List<Customer> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Page<Customer> findAll(Pageable pageable) {
    return jpaRepository.findAll(pageable).map(mapper::toDomain);
  }

  @Override
  public List<Customer> findBySegment(String segment) {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .filter(c -> c.getSegment().name().equals(segment))
        .toList();
  }

  @Override
  @CacheEvict(value = CACHE_NAME, allEntries = true)
  public void deleteById(CustomerId id) {
    jpaRepository.deleteById(id.getId());
  }
}
