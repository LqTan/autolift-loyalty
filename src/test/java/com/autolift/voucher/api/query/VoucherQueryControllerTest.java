package com.autolift.voucher.api.query;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.config.SecurityConfig;
import com.autolift.voucher.application.query.GetAllVouchersQuery;
import com.autolift.voucher.application.query.GetAllVouchersQueryHandler;
import com.autolift.voucher.application.query.GetVoucherQuery;
import com.autolift.voucher.application.query.GetVoucherQueryHandler;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VoucherQueryController.class)
@Import({GetVoucherQueryHandler.class, GetAllVouchersQueryHandler.class, SecurityConfig.class})
class VoucherQueryControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private GetVoucherQueryHandler getHandler;
  @MockBean private GetAllVouchersQueryHandler getAllHandler;

  @BeforeEach
  void setUp() {}

  @Test
  void shouldGetAllVouchers() throws Exception {
    List<VoucherResponse> vouchers =
        List.of(
            new VoucherResponse(
                "660e8400-e29b-41d4-a716-446655440000",
                "VCHR001",
                null,
                "DISCOUNT_PERCENTAGE",
                new BigDecimal("10"),
                new BigDecimal("100000"),
                100,
                0,
                "ACTIVE",
                null,
                Instant.parse("2026-12-31T23:59:59Z")),
            new VoucherResponse(
                "660e8400-e29b-41d4-a716-446655440001",
                "VCHR002",
                null,
                "DISCOUNT_FIXED_AMOUNT",
                new BigDecimal("50000"),
                new BigDecimal("200000"),
                50,
                5,
                "ACTIVE",
                null,
                Instant.parse("2026-12-31T23:59:59Z")));

    when(getAllHandler.handle(new GetAllVouchersQuery())).thenReturn(vouchers);

    mvc.perform(get("/api/vouchers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].code").value("VCHR001"))
        .andExpect(jsonPath("$[0].type").value("DISCOUNT_PERCENTAGE"))
        .andExpect(jsonPath("$[1].code").value("VCHR002"));
  }

  @Test
  void shouldGetVoucherByCode() throws Exception {
    VoucherResponse voucher =
        new VoucherResponse(
            "660e8400-e29b-41d4-a716-446655440000",
            "VCHR001",
            null,
            "DISCOUNT_PERCENTAGE",
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            0,
            "ACTIVE",
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    when(getHandler.handle(new GetVoucherQuery("VCHR001"))).thenReturn(voucher);

    mvc.perform(get("/api/vouchers/VCHR001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("VCHR001"))
        .andExpect(jsonPath("$.value").value(10))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }
}