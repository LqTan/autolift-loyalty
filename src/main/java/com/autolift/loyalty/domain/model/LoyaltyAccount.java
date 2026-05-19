package com.autolift.loyalty.domain.model;

import com.autolift.loyalty.domain.exception.InsufficientPointsException;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountStatus;
import com.autolift.loyalty.domain.valueobject.LoyaltyTier;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class LoyaltyAccount {

  private final LoyaltyAccountId id;
  private final String customerId;
  private BigDecimal pointsBalance;
  private LoyaltyTier tier;
  private LoyaltyAccountStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  protected LoyaltyAccount() {
    this.id = null;
    this.customerId = null;
    this.pointsBalance = null;
    this.tier = null;
    this.status = null;
    this.createdAt = null;
    this.updatedAt = null;
  }

  private LoyaltyAccount(
      LoyaltyAccountId id,
      String customerId,
      BigDecimal pointsBalance,
      LoyaltyTier tier,
      LoyaltyAccountStatus status,
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

  public static LoyaltyAccount create(String customerId) {
    LoyaltyAccountId id = LoyaltyAccountId.random();
    Instant now = Instant.now();
    return new LoyaltyAccount(
        id, customerId, BigDecimal.ZERO, LoyaltyTier.BRONZE, LoyaltyAccountStatus.ACTIVE, now, now);
  }

  public static LoyaltyAccount of(
      LoyaltyAccountId id,
      String customerId,
      BigDecimal pointsBalance,
      LoyaltyTier tier,
      LoyaltyAccountStatus status,
      Instant createdAt,
      Instant updatedAt) {
    return new LoyaltyAccount(id, customerId, pointsBalance, tier, status, createdAt, updatedAt);
  }

  public void addPoints(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    this.pointsBalance = this.pointsBalance.add(amount);
    this.tier = this.tier.upgrade(this.pointsBalance);
    this.updatedAt = Instant.now();
  }

  public void deductPoints(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }
    if (this.pointsBalance.compareTo(amount) < 0) {
      throw new InsufficientPointsException(
          "Insufficient points. Available: " + this.pointsBalance + ", Requested: " + amount);
    }
    this.pointsBalance = this.pointsBalance.subtract(amount);
    this.tier = LoyaltyTier.fromPoints(this.pointsBalance);
    this.updatedAt = Instant.now();
  }

  public void suspend() {
    if (this.status != LoyaltyAccountStatus.ACTIVE) {
      throw new IllegalStateException("Cannot suspend account with status: " + this.status);
    }
    this.status = LoyaltyAccountStatus.SUSPENDED;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    if (this.status != LoyaltyAccountStatus.SUSPENDED) {
      throw new IllegalStateException("Cannot activate account with status: " + this.status);
    }
    this.status = LoyaltyAccountStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public void close() {
    if (this.status == LoyaltyAccountStatus.CLOSED) {
      return;
    }
    this.status = LoyaltyAccountStatus.CLOSED;
    this.updatedAt = Instant.now();
  }
}
