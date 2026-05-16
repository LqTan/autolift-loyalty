package com.autolift.promotion.api.query;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.config.SecurityConfig;
import com.autolift.promotion.application.query.GetAllPromotionsQueryHandler;
import com.autolift.promotion.application.query.GetPromotionQuery;
import com.autolift.promotion.application.query.GetPromotionQueryHandler;
import com.autolift.promotion.application.query.PromotionView;
import com.autolift.promotion.domain.valueobject.PromotionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PromotionQueryController.class)
@Import({GetPromotionQueryHandler.class, GetAllPromotionsQueryHandler.class, SecurityConfig.class})
class PromotionQueryControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private GetPromotionQueryHandler getByIdHandler;

  @MockBean private GetAllPromotionsQueryHandler getAllHandler;

  @Test
  void shouldGetPromotionById() throws Exception {
    PromotionView view =
        new PromotionView(
            "550e8400-e29b-41d4-a716-446655440000",
            "Summer Sale",
            "20% off",
            PromotionType.PERCENTAGE,
            new BigDecimal("20"),
            new BigDecimal("100000"),
            "VIP",
            "ACTIVE",
            Instant.parse("2026-06-01T00:00:00Z"),
            Instant.parse("2026-06-30T23:59:59Z"),
            Instant.now(),
            Instant.now());

    when(getByIdHandler.handle(new GetPromotionQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(view);

    mvc.perform(get("/api/promotions/550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(jsonPath("$.name").value("Summer Sale"))
        .andExpect(jsonPath("$.promotionType").value("PERCENTAGE"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void shouldGetAllPromotions() throws Exception {
    PromotionView view1 =
        new PromotionView(
            "550e8400-e29b-41d4-a716-446655440000",
            "Promo 1",
            "Description 1",
            PromotionType.PERCENTAGE,
            new BigDecimal("10"),
            null,
            null,
            "DRAFT",
            null,
            null,
            Instant.now(),
            Instant.now());

    PromotionView view2 =
        new PromotionView(
            "660e8400-e29b-41d4-a716-446655440001",
            "Promo 2",
            "Description 2",
            PromotionType.FIXED_AMOUNT,
            new BigDecimal("50000"),
            new BigDecimal("100000"),
            "VIP",
            "ACTIVE",
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            Instant.now(),
            Instant.now());

    when(getAllHandler.handle()).thenReturn(List.of(view1, view2));

    mvc.perform(get("/api/promotions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(jsonPath("$[0].name").value("Promo 1"))
        .andExpect(jsonPath("$[1].id").value("660e8400-e29b-41d4-a716-446655440001"))
        .andExpect(jsonPath("$[1].name").value("Promo 2"));
  }
}
