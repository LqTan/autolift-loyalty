package com.autolift.promotion.domain.model;

import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;

@Getter
public class Promotion {

  private final PromotionId id;
  private final String name;
  private final String description;
  private final PromotionType promotionType;
  private final BigDecimal value;
  private final BigDecimal minOrderAmount;
  private final String applicableCustomerSegment;
  private PromotionStatus status;
  private final Instant startDate;
  private final Instant endDate;
  private final Instant createdAt;
  private Instant updatedAt;

  protected Promotion() {
    this.id = null;
    this.name = null;
    this.description = null;
    this.promotionType = null;
    this.value = null;
    this.minOrderAmount = null;
    this.applicableCustomerSegment = null;
    this.status = null;
    this.startDate = null;
    this.endDate = null;
    this.createdAt = null;
    this.updatedAt = null;
  }

  private Promotion(
      PromotionId id,
      String name,
      String description,
      PromotionType promotionType,
      BigDecimal value,
      BigDecimal minOrderAmount,
      String applicableCustomerSegment,
      PromotionStatus status,
      Instant startDate,
      Instant endDate,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.promotionType = promotionType;
    this.value = value;
    this.minOrderAmount = minOrderAmount;
    this.applicableCustomerSegment = applicableCustomerSegment;
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Promotion create(
      String name,
      String description,
      PromotionType promotionType,
      BigDecimal value,
      BigDecimal minOrderAmount,
      String applicableCustomerSegment,
      Instant startDate,
      Instant endDate) {
    PromotionId id = PromotionId.random();
    Instant now = Instant.now();
    return new Promotion(
        id,
        name,
        description,
        promotionType,
        value,
        minOrderAmount,
        applicableCustomerSegment,
        PromotionStatus.DRAFT,
        startDate,
        endDate,
        now,
        now);
  }

  public static Promotion of(
      PromotionId id,
      String name,
      String description,
      PromotionType promotionType,
      BigDecimal value,
      BigDecimal minOrderAmount,
      String applicableCustomerSegment,
      PromotionStatus status,
      Instant startDate,
      Instant endDate,
      Instant createdAt,
      Instant updatedAt) {
    return new Promotion(
        id,
        name,
        description,
        promotionType,
        value,
        minOrderAmount,
        applicableCustomerSegment,
        status,
        startDate,
        endDate,
        createdAt,
        updatedAt);
  }

  public void activate() {
    if (this.status != PromotionStatus.DRAFT) {
      throw new IllegalStateException("Cannot activate promotion with status: " + this.status);
    }
    this.status = PromotionStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public void deactivate() {
    if (this.status != PromotionStatus.ACTIVE) {
      throw new IllegalStateException("Cannot deactivate promotion with status: " + this.status);
    }
    this.status = PromotionStatus.INACTIVE;
    this.updatedAt = Instant.now();
  }

  public void expire() {
    if (this.status == PromotionStatus.EXPIRED || this.status == PromotionStatus.INACTIVE) {
      return;
    }
    this.status = PromotionStatus.EXPIRED;
    this.updatedAt = Instant.now();
  }

  public boolean isValid() {
    Instant now = Instant.now();
    return status == PromotionStatus.ACTIVE
        && (startDate == null || now.isAfter(startDate))
        && (endDate == null || now.isBefore(endDate));
  }
}
