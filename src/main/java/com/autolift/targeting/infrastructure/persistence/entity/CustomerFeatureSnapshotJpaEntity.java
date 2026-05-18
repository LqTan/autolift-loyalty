package com.autolift.targeting.infrastructure.persistence.entity;

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
@Table(name = "customer_feature_snapshots", schema = "targeting")
public class CustomerFeatureSnapshotJpaEntity {

  @Id private UUID id;

  @Column(name = "customer_id") private String customerId;

  @Column(name = "campaign_id") private String campaignId;

  @Column(name = "recency_days") private Integer recencyDays;

  @Column(name = "frequency_90d") private Integer frequency90d;

  @Column(name = "monetary_90d", precision = 14, scale = 2) private BigDecimal monetary90d;

  @Column(name = "avg_basket_value", precision = 14, scale = 2) private BigDecimal avgBasketValue;

  @Column(name = "total_quantity_90d", precision = 14, scale = 2) private BigDecimal totalQuantity90d;

  @Column(name = "unique_product_count") private Integer uniqueProductCount;

  @Column(name = "unique_category_count") private Integer uniqueCategoryCount;

  @Column(name = "favorite_category") private String favoriteCategory;

  @Column(name = "feature_version") private String featureVersion;

  @Column(name = "created_at") private Instant createdAt;

  public CustomerFeatureSnapshotJpaEntity(
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
}