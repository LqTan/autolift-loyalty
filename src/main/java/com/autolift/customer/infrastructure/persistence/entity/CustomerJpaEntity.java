package com.autolift.customer.infrastructure.persistence.entity;

import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers", schema = "customer")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CustomerJpaEntity {

  @Id private UUID id;

  @Column private String name;
  @Column private String email;
  @Column private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "segment")
  private CustomerSegment segment;

  @Enumerated(EnumType.STRING)
  @Column
  private CustomerStatus status;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  public CustomerJpaEntity(
      UUID id,
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

  public static CustomerJpaEntity of(
      UUID id,
      String name,
      String email,
      String phone,
      CustomerSegment segment,
      CustomerStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new CustomerJpaEntity(id, name, email, phone, segment, status, createdAt, updatedAt);
  }
}
