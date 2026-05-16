package com.autolift.promotion.infrastructure.persistence.entity;

import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotions", schema = "promotion")
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class PromotionJpaEntity {

  @Id private UUID id;

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private PromotionType promotionType;

  private BigDecimal value;
  private BigDecimal minOrderAmount;
  private String applicableCustomerSegment;

  @Enumerated(EnumType.STRING)
  private PromotionStatus status;

  private Instant startDate;
  private Instant endDate;
  private Instant createdAt;
  private Instant updatedAt;

  private PromotionJpaEntity(
      UUID id,
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

  public static PromotionJpaEntity of(
      UUID id,
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
    return new PromotionJpaEntity(
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
}
