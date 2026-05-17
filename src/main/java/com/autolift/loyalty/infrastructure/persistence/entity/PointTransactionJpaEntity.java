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
@Table(name = "point_transactions", schema = "loyalty")
public class PointTransactionJpaEntity {

  @Id private UUID id;

  @Column(name = "loyalty_account_id") private UUID loyaltyAccountId;

  @Column private BigDecimal amount;

  @Column(name = "transaction_type") private String transactionType;

  @Column(name = "reference_id") private String referenceId;

  @Column(name = "created_at") private Instant createdAt;

  public PointTransactionJpaEntity(
      UUID id,
      UUID loyaltyAccountId,
      BigDecimal amount,
      String transactionType,
      String referenceId,
      Instant createdAt) {
    this.id = id;
    this.loyaltyAccountId = loyaltyAccountId;
    this.amount = amount;
    this.transactionType = transactionType;
    this.referenceId = referenceId;
    this.createdAt = createdAt;
  }
}