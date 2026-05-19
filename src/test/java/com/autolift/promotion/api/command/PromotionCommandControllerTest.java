package com.autolift.promotion.api.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.auth.ApplicationUserDetailsService;
import com.autolift.auth.JwtTokenProvider;
import com.autolift.config.SecurityConfig;
import com.autolift.promotion.application.command.ActivatePromotionCommandHandler;
import com.autolift.promotion.application.command.CreatePromotionCommand;
import com.autolift.promotion.application.command.CreatePromotionCommandHandler;
import com.autolift.promotion.application.command.CreatePromotionResult;
import com.autolift.promotion.application.command.DeactivatePromotionCommandHandler;
import com.autolift.promotion.domain.valueobject.PromotionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PromotionCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  CreatePromotionCommandHandler.class,
  ActivatePromotionCommandHandler.class,
  DeactivatePromotionCommandHandler.class,
  SecurityConfig.class
})
class PromotionCommandControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CreatePromotionCommandHandler createHandler;
  @MockBean private ActivatePromotionCommandHandler activateHandler;
  @MockBean private DeactivatePromotionCommandHandler deactivateHandler;
  @MockBean private JwtTokenProvider jwtTokenProvider;
  @MockBean private ApplicationUserDetailsService userDetailsService;
  @MockBean private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void shouldCreatePromotion() throws Exception {
    CreatePromotionRequest request =
        new CreatePromotionRequest(
            "Summer Sale",
            "20% off summer",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    CreatePromotionResult result =
        new CreatePromotionResult(
            "550e8400-e29b-41d4-a716-446655440000",
            "Summer Sale",
            "20% off summer",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            "DRAFT",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"));

    when(createHandler.handle(any(CreatePromotionCommand.class))).thenReturn(result);

    mvc.perform(
            post("/api/promotions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(jsonPath("$.name").value("Summer Sale"))
        .andExpect(jsonPath("$.promotionType").value("PERCENTAGE"))
        .andExpect(jsonPath("$.status").value("DRAFT"));
  }

  @Test
  void shouldActivatePromotion() throws Exception {
    mvc.perform(post("/api/promotions/550e8400-e29b-41d4-a716-446655440000/activate"))
        .andExpect(status().isOk());
  }

  @Test
  void shouldDeactivatePromotion() throws Exception {
    mvc.perform(post("/api/promotions/550e8400-e29b-41d4-a716-446655440000/deactivate"))
        .andExpect(status().isNoContent());
  }
}
