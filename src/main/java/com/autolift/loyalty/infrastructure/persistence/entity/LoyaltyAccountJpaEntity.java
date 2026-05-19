package com.autolift.loyalty.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "loyalty_accounts", schema = "loyalty")
public class LoyaltyAccountJpaEntity {

  @Id private UUID id;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "points_balance")
  private BigDecimal pointsBalance;

  @Column private String tier;

  @Column private String status;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  public LoyaltyAccountJpaEntity(
      UUID id,
      String customerId,
      BigDecimal pointsBalance,
      String tier,
      String status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.customerId = customerId;
    this.pointsBalance = pointsBalance;
    this.tier = tier;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
