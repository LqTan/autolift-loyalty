package com.autolift.targeting.api.query;

import java.math.BigDecimal;
import java.time.Instant;

public record CustomerFeatureResponse(
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