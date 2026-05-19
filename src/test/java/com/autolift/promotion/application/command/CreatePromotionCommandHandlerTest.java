package com.autolift.promotion.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.domain.valueobject.PromotionStatus;
import com.autolift.promotion.domain.valueobject.PromotionType;
import com.autolift.promotion.events.PromotionCreatedEvent;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CreatePromotionCommandHandlerTest {

  @Mock private PromotionRepository repository;
  @Mock private ApplicationEventPublisher eventPublisher;

  private CreatePromotionCommandHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreatePromotionCommandHandler(repository, eventPublisher);
  }

  @Test
  void shouldPublishPromotionCreatedEvent() {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Summer Sale",
            "20% off summer",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    handler.handle(command);

    ArgumentCaptor<PromotionCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(PromotionCreatedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    PromotionCreatedEvent event = eventCaptor.getValue();
    assertThat(event.getName()).isEqualTo("Summer Sale");
  }

  @Test
  void shouldCreatePromotionWithCorrectData() {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Summer Sale",
            "20% off summer",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreatePromotionResult result = handler.handle(command);

    assertThat(result.id()).isNotNull();
    assertThat(result.name()).isEqualTo("Summer Sale");
    assertThat(result.description()).isEqualTo("20% off summer");
    assertThat(result.promotionType()).isEqualTo(PromotionType.PERCENTAGE);
    assertThat(result.value()).isEqualByComparingTo("20");
    assertThat(result.minOrderAmount()).isEqualByComparingTo("100000");
    assertThat(result.applicableCustomerSegment()).isEqualTo("VIP");
    assertThat(result.status()).isEqualTo(PromotionStatus.DRAFT.name());
    assertThat(result.startDate()).isEqualTo(Instant.parse("2026-06-01T00:00:00Z"));
    assertThat(result.endDate()).isEqualTo(Instant.parse("2026-06-30T23:59:59Z"));
  }

  @Test
  void shouldSavePromotionToRepository() {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Test",
            null,
            PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            null,
            null,
            null,
            null);

    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    handler.handle(command);

    ArgumentCaptor<Promotion> captor = ArgumentCaptor.forClass(Promotion.class);
    verify(repository).save(captor.capture());

    Promotion savedPromotion = captor.getValue();
    assertThat(savedPromotion.getName()).isEqualTo("Test");
    assertThat(savedPromotion.getPromotionType()).isEqualTo(PromotionType.FIXED_AMOUNT);
    assertThat(savedPromotion.getValue()).isEqualByComparingTo("50000");
  }

  @Test
  void shouldCreatePromotionWithNullOptionalFields() {
    CreatePromotionCommand command =
        new CreatePromotionCommand(
            "Minimal",
            null,
            PromotionType.PERCENTAGE,
            new BigDecimal("15"),
            null,
            null,
            null,
            null);

    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreatePromotionResult result = handler.handle(command);

    assertThat(result.name()).isEqualTo("Minimal");
    assertThat(result.description()).isNull();
    assertThat(result.minOrderAmount()).isNull();
    assertThat(result.applicableCustomerSegment()).isNull();
    assertThat(result.startDate()).isNull();
    assertThat(result.endDate()).isNull();
  }
}
