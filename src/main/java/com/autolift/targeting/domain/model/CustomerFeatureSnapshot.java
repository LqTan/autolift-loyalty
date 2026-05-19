package com.autolift.targeting.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class CustomerFeatureSnapshot {

  private final UUID id;
  private final String customerId;
  private final String campaignId;
  private final Integer recencyDays;
  private final Integer frequency90d;
  private final BigDecimal monetary90d;
  private final BigDecimal avgBasketValue;
  private final BigDecimal totalQuantity90d;
  private final Integer uniqueProductCount;
  private final Integer uniqueCategoryCount;
  private final String favoriteCategory;
  private final String featureVersion;
  private final Instant createdAt;

  protected CustomerFeatureSnapshot() {
    this.id = null;
    this.customerId = null;
    this.campaignId = null;
    this.recencyDays = null;
    this.frequency90d = null;
    this.monetary90d = null;
    this.avgBasketValue = null;
    this.totalQuantity90d = null;
    this.uniqueProductCount = null;
    this.uniqueCategoryCount = null;
    this.favoriteCategory = null;
    this.featureVersion = null;
    this.createdAt = null;
  }

  private CustomerFeatureSnapshot(
      UUID id,
      String customerId,
      String campaignId,
      Integer recencyDays,
      Integer frequency90d,
      BigDecimal monetary90d,
      BigDecimal avgBasketValue,
      BigDecimal totalQuantity90d,
      Integer uniqueProductCount,
      Integer uniqueCategoryCount,
      String favoriteCategory,
      String featureVersion,
      Instant createdAt) {
    this.id = id;
    this.customerId = customerId;
    this.campaignId = campaignId;
    this.recencyDays = recencyDays;
    this.frequency90d = frequency90d;
    this.monetary90d = monetary90d;
    this.avgBasketValue = avgBasketValue;
    this.totalQuantity90d = totalQuantity90d;
    this.uniqueProductCount = uniqueProductCount;
    this.uniqueCategoryCount = uniqueCategoryCount;
    this.favoriteCategory = favoriteCategory;
    this.featureVersion = featureVersion;
    this.createdAt = createdAt;
  }

  public static CustomerFeatureSnapshot create(
      String customerId,
      String campaignId,
      Integer recencyDays,
      Integer frequency90d,
      BigDecimal monetary90d,
      BigDecimal avgBasketValue,
      BigDecimal totalQuantity90d,
      Integer uniqueProductCount,
      Integer uniqueCategoryCount,
      String favoriteCategory,
      String featureVersion) {
    return new CustomerFeatureSnapshot(
        UUID.randomUUID(),
        customerId,
        campaignId,
        recencyDays,
        frequency90d,
        monetary90d,
        avgBasketValue,
        totalQuantity90d,
        uniqueProductCount,
        uniqueCategoryCount,
        favoriteCategory,
        featureVersion,
        Instant.now());
  }

  public static CustomerFeatureSnapshot of(
      UUID id,
      String customerId,
      String campaignId,
      Integer recencyDays,
      Integer frequency90d,
      BigDecimal monetary90d,
      BigDecimal avgBasketValue,
      BigDecimal totalQuantity90d,
      Integer uniqueProductCount,
      Integer uniqueCategoryCount,
      String favoriteCategory,
      String featureVersion,
      Instant createdAt) {
    return new CustomerFeatureSnapshot(
        id,
        customerId,
        campaignId,
        recencyDays,
        frequency90d,
        monetary90d,
        avgBasketValue,
        totalQuantity90d,
        uniqueProductCount,
        uniqueCategoryCount,
        favoriteCategory,
        featureVersion,
        createdAt);
  }
}
