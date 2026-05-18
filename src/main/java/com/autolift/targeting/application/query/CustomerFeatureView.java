package com.autolift.targeting.application.query;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerFeatureView(
    String customerId,
    Integer recencyDays,
    Integer frequency90d,
    BigDecimal monetary90d,
    BigDecimal avgBasketValue,
    BigDecimal totalQuantity90d,
    Integer uniqueProductCount,
    Integer uniqueCategoryCount,
    String favoriteCategory,
    String featureVersion,
    Instant createdAt) {}