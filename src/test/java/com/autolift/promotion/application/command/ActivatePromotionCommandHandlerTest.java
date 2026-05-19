package com.autolift.promotion.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.autolift.promotion.domain.exception.PromotionNotFoundException;
import com.autolift.promotion.domain.model.Promotion;
import com.autolift.promotion.domain.repository.PromotionRepository;
import com.autolift.promotion.events.PromotionActivatedEvent;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class ActivatePromotionCommandHandlerTest {

  @Mock private PromotionRepository repository;
  @Mock private ApplicationEventPublisher eventPublisher;

  private ActivatePromotionCommandHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ActivatePromotionCommandHandler(repository, eventPublisher);
  }

  @Test
  void shouldActivateDraftPromotion() {
    String promotionId = UUID.randomUUID().toString();
    Promotion promotion = Promotion.create("Test", null, null, null, null, null, null, null);

    when(repository.findById(any())).thenReturn(Optional.of(promotion));
    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ActivatePromotionCommand command = new ActivatePromotionCommand(promotionId);
    handler.handle(command);

    verify(repository).save(promotion);
  }

  @Test
  void shouldPublishPromotionActivatedEvent() {
    String promotionId = UUID.randomUUID().toString();
    Promotion promotion = Promotion.create("Test", null, null, null, null, null, null, null);

    when(repository.findById(any())).thenReturn(Optional.of(promotion));
    when(repository.save(any(Promotion.class))).thenAnswer(invocation -> invocation.getArgument(0));

    ActivatePromotionCommand command = new ActivatePromotionCommand(promotionId);
    handler.handle(command);

    ArgumentCaptor<PromotionActivatedEvent> eventCaptor =
        ArgumentCaptor.forClass(PromotionActivatedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    PromotionActivatedEvent event = eventCaptor.getValue();
    assertThat(event.getName()).isEqualTo("Test");
  }

  @Test
  void shouldThrowExceptionWhenPromotionNotFound() {
    String promotionId = UUID.randomUUID().toString();

    when(repository.findById(any())).thenReturn(Optional.empty());

    ActivatePromotionCommand command = new ActivatePromotionCommand(promotionId);

    assertThatThrownBy(() -> handler.handle(command))
        .isInstanceOf(PromotionNotFoundException.class)
        .hasMessageContaining(promotionId);
  }
}
