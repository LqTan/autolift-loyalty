package com.autolift.promotion.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PromotionTest {

  @Test
  void shouldCreatePromotionWithDraftStatus() {
    Promotion promotion =
        Promotion.create(
            "Summer Sale 20%",
            "20% off for summer",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    assertThat(promotion.getId()).isNotNull();
    assertThat(promotion.getName()).isEqualTo("Summer Sale 20%");
    assertThat(promotion.getDescription()).isEqualTo("20% off for summer");
    assertThat(promotion.getPromotionType()).isEqualTo(PromotionType.PERCENTAGE);
    assertThat(promotion.getValue()).isEqualByComparingTo("20");
    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.DRAFT);
  }

  @Test
  void shouldActivateDraftPromotion() {
    Promotion promotion =
        Promotion.create(
            "Fixed 50k Discount",
            "50k off",
            PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            null,
            null,
            Instant.now(),
            null);

    promotion.activate();

    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
  }

  @Test
  void shouldRejectActivationOfNonDraftPromotion() {
    Promotion promotion =
        Promotion.create(
            "Test", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);
    promotion.activate();

    assertThatThrownBy(promotion::activate)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot activate promotion with status: ACTIVE");
  }

  @Test
  void shouldDeactivateActivePromotion() {
    Promotion promotion =
        Promotion.create(
            "Test", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);
    promotion.activate();

    promotion.deactivate();

    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.INACTIVE);
  }

  @Test
  void shouldRejectDeactivateOfNonActivePromotion() {
    Promotion promotion =
        Promotion.create(
            "Test", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);

    assertThatThrownBy(promotion::deactivate)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Cannot deactivate promotion with status: DRAFT");
  }

  @Test
  void shouldExpirePromotion() {
    Promotion promotion =
        Promotion.create(
            "Test", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);
    promotion.activate();

    promotion.expire();

    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.EXPIRED);
  }

  @Test
  void shouldNotChangeStatusWhenExpireAlreadyInactiveOrExpired() {
    Promotion promotion =
        Promotion.create(
            "Test", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);
    promotion.activate();
    promotion.deactivate();

    promotion.expire();

    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.INACTIVE);
  }

  @Test
  void shouldCreatePromotionWithoutOptionalFields() {
    Promotion promotion =
        Promotion.create(
            "Minimal Promotion",
            null,
            PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            null,
            null,
            null,
            null);

    assertThat(promotion.getId()).isNotNull();
    assertThat(promotion.getName()).isEqualTo("Minimal Promotion");
    assertThat(promotion.getDescription()).isNull();
    assertThat(promotion.getMinOrderAmount()).isNull();
    assertThat(promotion.getApplicableCustomerSegment()).isNull();
    assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.DRAFT);
  }

  @Test
  void shouldCheckValidity() {
    Promotion activePromotion =
        Promotion.create(
            "Active",
            null,
            PromotionType.PERCENTAGE,
            new BigDecimal("10"),
            null,
            null,
            Instant.now().minusSeconds(3600),
            Instant.now().plusSeconds(3600));
    activePromotion.activate();

    assertThat(activePromotion.isValid()).isTrue();

    Promotion draftPromotion =
        Promotion.create(
            "Draft", null, PromotionType.PERCENTAGE, new BigDecimal("10"), null, null, null, null);

    assertThat(draftPromotion.isValid()).isFalse();
  }
}
