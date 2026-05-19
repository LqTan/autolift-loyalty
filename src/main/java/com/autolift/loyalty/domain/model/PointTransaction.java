package com.autolift.loyalty.domain.model;

import com.autolift.loyalty.domain.valueobject.PointTransactionId;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class PointTransaction {

  private final PointTransactionId id;
  private final PointTransactionId loyaltyAccountId;
  private final BigDecimal amount;
  private final TransactionType transactionType;
  private final String referenceId;
  private final Instant createdAt;

  protected PointTransaction() {
    this.id = null;
    this.loyaltyAccountId = null;
    this.amount = null;
    this.transactionType = null;
    this.referenceId = null;
    this.createdAt = null;
  }

  private PointTransaction(
      PointTransactionId id,
      PointTransactionId loyaltyAccountId,
      BigDecimal amount,
      TransactionType transactionType,
      String referenceId,
      Instant createdAt) {
    this.id = id;
    this.loyaltyAccountId = loyaltyAccountId;
    this.amount = amount;
    this.transactionType = transactionType;
    this.referenceId = referenceId;
    this.createdAt = createdAt;
  }

  public static PointTransaction create(
      PointTransactionId loyaltyAccountId,
      BigDecimal amount,
      TransactionType transactionType,
      String referenceId) {
    return new PointTransaction(
        PointTransactionId.random(),
        loyaltyAccountId,
        amount,
        transactionType,
        referenceId,
        Instant.now());
  }

  public static PointTransaction of(
      PointTransactionId id,
      PointTransactionId loyaltyAccountId,
      BigDecimal amount,
      TransactionType transactionType,
      String referenceId,
      Instant createdAt) {
    return new PointTransaction(
        id, loyaltyAccountId, amount, transactionType, referenceId, createdAt);
  }

  public enum TransactionType {
    EARN,
    REDEEM,
    EXPIRE,
    ADJUST
  }
}
