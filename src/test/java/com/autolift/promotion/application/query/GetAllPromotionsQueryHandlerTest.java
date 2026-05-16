package com.autolift.promotion.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllPromotionsQueryHandlerTest {

  @Mock private PromotionRepository repository;

  private GetAllPromotionsQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetAllPromotionsQueryHandler(repository);
  }

  @Test
  void shouldReturnAllPromotions() {
    Promotion promo1 =
        Promotion.of(
            PromotionId.random(),
            "Promo 1",
            "Description 1",
            PromotionType.PERCENTAGE,
            new BigDecimal("10"),
            null,
            null,
            PromotionStatus.DRAFT,
            null,
            null,
            Instant.now(),
            Instant.now());

    Promotion promo2 =
        Promotion.of(
            PromotionId.random(),
            "Promo 2",
            "Description 2",
            PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            new BigDecimal("100000"),
            "VIP",
            PromotionStatus.ACTIVE,
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            Instant.now(),
            Instant.now());

    when(repository.findAll()).thenReturn(List.of(promo1, promo2));

    List<PromotionView> views = handler.handle();

    assertThat(views).hasSize(2);
    assertThat(views.get(0).name()).isEqualTo("Promo 1");
    assertThat(views.get(1).name()).isEqualTo("Promo 2");
  }

  @Test
  void shouldReturnEmptyListWhenNoPromotions() {
    when(repository.findAll()).thenReturn(List.of());

    List<PromotionView> views = handler.handle();

    assertThat(views).isEmpty();
  }

  @Test
  void shouldMapAllFieldsCorrectly() {
    Promotion promo =
        Promotion.of(
            PromotionId.random(),
            "Test",
            "Test Description",
            PromotionType.PERCENTAGE,
            new BigDecimal("25"),
            new BigDecimal("200000"),
            "SILVER",
            PromotionStatus.ACTIVE,
            Instant.parse("2026-05-01T00:00:00Z"),
            Instant.parse("2026-05-31T23:59:59Z"),
            Instant.parse("2026-04-15T10:30:00Z"),
            Instant.parse("2026-04-20T14:00:00Z"));

    when(repository.findAll()).thenReturn(List.of(promo));

    List<PromotionView> views = handler.handle();

    assertThat(views).hasSize(1);
    PromotionView view = views.get(0);
    assertThat(view.name()).isEqualTo("Test");
    assertThat(view.description()).isEqualTo("Test Description");
    assertThat(view.promotionType()).isEqualTo(PromotionType.PERCENTAGE);
    assertThat(view.value()).isEqualByComparingTo("25");
    assertThat(view.minOrderAmount()).isEqualByComparingTo("200000");
    assertThat(view.applicableCustomerSegment()).isEqualTo("SILVER");
    assertThat(view.status()).isEqualTo("ACTIVE");
    assertThat(view.startDate()).isEqualTo(Instant.parse("2026-05-01T00:00:00Z"));
    assertThat(view.endDate()).isEqualTo(Instant.parse("2026-05-31T23:59:59Z"));
  }
}
