package com.autolift.customer.infrastructure.persistence.mapper;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.valueobject.CustomerId;
import com.autolift.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

  public Customer toDomain(CustomerJpaEntity entity) {
    if (entity == null) {
      return null;
    }
    return Customer.of(
        CustomerId.of(entity.getId()),
        entity.getName(),
        entity.getEmail(),
        entity.getPhone(),
        entity.getSegment(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  public CustomerJpaEntity toEntity(Customer domain) {
    if (domain == null) {
      return null;
    }
    return CustomerJpaEntity.of(
        domain.getId().getId(),
        domain.getName(),
        domain.getEmail(),
        domain.getPhone(),
        domain.getSegment(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }
}