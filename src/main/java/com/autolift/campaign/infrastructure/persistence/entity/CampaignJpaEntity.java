package com.autolift.campaign.infrastructure.persistence.entity;

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
@Table(name = "campaigns", schema = "campaign")
public class CampaignJpaEntity {

  @Id private UUID id;

  @Column private String name;

  @Column private String description;

  @Column private String status;

  @Column private Instant startDate;

  @Column private Instant endDate;

  @Column private BigDecimal budgetAmount;

  @Column private String budgetCurrency;

  @Column private Instant createdAt;

  @Column private Instant updatedAt;

  public CampaignJpaEntity(
      UUID id,
      String name,
      String description,
      String status,
      Instant startDate,
      Instant endDate,
      BigDecimal budgetAmount,
      String budgetCurrency,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.status = status;
    this.startDate = startDate;
    this.endDate = endDate;
    this.budgetAmount = budgetAmount;
    this.budgetCurrency = budgetCurrency;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
