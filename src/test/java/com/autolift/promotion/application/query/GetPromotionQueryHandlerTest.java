package com.autolift.promotion.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autolift.promotion.domain.exception.PromotionNotFoundException;
import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionId;
import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetPromotionQueryHandlerTest {

  @Mock private PromotionRepository repository;

  private GetPromotionQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetPromotionQueryHandler(repository);
  }

  @Test
  void shouldReturnPromotionViewWhenFound() {
    UUID id = UUID.randomUUID();
    Promotion promotion =
        Promotion.of(
            PromotionId.of(id),
            "Test Promotion",
            "Description",
            PromotionType.PERCENTAGE,
            new BigDecimal("25"),
            new BigDecimal("50000"),
            "GOLD",
            PromotionStatus.ACTIVE,
            Instant.parse("2026-05-01T00:00:00Z"),
            Instant.parse("2026-05-31T23:59:59Z"),
            Instant.now(),
            Instant.now());

    when(repository.findById(PromotionId.of(id.toString()))).thenReturn(Optional.of(promotion));

    PromotionView view = handler.handle(new GetPromotionQuery(id.toString()));

    assertThat(view.id()).isEqualTo(id.toString());
    assertThat(view.name()).isEqualTo("Test Promotion");
    assertThat(view.description()).isEqualTo("Description");
    assertThat(view.promotionType()).isEqualTo(PromotionType.PERCENTAGE);
    assertThat(view.value()).isEqualByComparingTo("25");
    assertThat(view.minOrderAmount()).isEqualByComparingTo("50000");
    assertThat(view.applicableCustomerSegment()).isEqualTo("GOLD");
    assertThat(view.status()).isEqualTo("ACTIVE");
  }

  @Test
  void shouldThrowExceptionWhenNotFound() {
    UUID id = UUID.randomUUID();
    when(repository.findById(PromotionId.of(id.toString()))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(new GetPromotionQuery(id.toString())))
        .isInstanceOf(PromotionNotFoundException.class)
        .hasMessageContaining(id.toString());
  }

  @Test
  void shouldQueryRepositoryWithCorrectId() {
    UUID id = UUID.randomUUID();
    when(repository.findById(PromotionId.of(id.toString()))).thenReturn(Optional.empty());

    try {
      handler.handle(new GetPromotionQuery(id.toString()));
    } catch (Exception ignored) {
    }

    verify(repository).findById(PromotionId.of(id.toString()));
  }
}
