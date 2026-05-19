package com.autolift.voucher.api.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.auth.ApplicationUserDetailsService;
import com.autolift.auth.JwtTokenProvider;
import com.autolift.config.SecurityConfig;
import com.autolift.voucher.application.command.CreateVoucherCommand;
import com.autolift.voucher.application.command.CreateVoucherCommandHandler;
import com.autolift.voucher.application.command.CreateVoucherResult;
import com.autolift.voucher.application.command.RedeemVoucherCommandHandler;
import com.autolift.voucher.domain.valueobject.VoucherStatus;
import com.autolift.voucher.domain.valueobject.VoucherType;
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

@WebMvcTest(VoucherCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({CreateVoucherCommandHandler.class, RedeemVoucherCommandHandler.class, SecurityConfig.class})
class VoucherCommandControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CreateVoucherCommandHandler createHandler;
  @MockBean private RedeemVoucherCommandHandler redeemHandler;
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
  void shouldCreateVoucherWithPercentageType() throws Exception {
    CreateVoucherRequest request =
        new CreateVoucherRequest(
            "VCHR001",
            null,
            "PERCENTAGE",
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    CreateVoucherResult result =
        new CreateVoucherResult(
            "660e8400-e29b-41d4-a716-446655440000",
            "VCHR001",
            null,
            VoucherType.DISCOUNT_PERCENTAGE,
            new BigDecimal("10"),
            new BigDecimal("100000"),
            100,
            0,
            VoucherStatus.ACTIVE,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    when(createHandler.handle(any(CreateVoucherCommand.class))).thenReturn(result);

    mvc.perform(
            post("/api/vouchers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("VCHR001"))
        .andExpect(jsonPath("$.type").value("DISCOUNT_PERCENTAGE"))
        .andExpect(jsonPath("$.value").value(10))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void shouldCreateVoucherWithFixedType() throws Exception {
    CreateVoucherRequest request =
        new CreateVoucherRequest(
            "VCHR002",
            null,
            "FIXED",
            new BigDecimal("50000"),
            new BigDecimal("200000"),
            50,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    CreateVoucherResult result =
        new CreateVoucherResult(
            "660e8400-e29b-41d4-a716-446655440001",
            "VCHR002",
            null,
            VoucherType.DISCOUNT_FIXED_AMOUNT,
            new BigDecimal("50000"),
            new BigDecimal("200000"),
            50,
            0,
            VoucherStatus.ACTIVE,
            null,
            Instant.parse("2026-12-31T23:59:59Z"));

    when(createHandler.handle(any(CreateVoucherCommand.class))).thenReturn(result);

    mvc.perform(
            post("/api/vouchers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.code").value("VCHR002"))
        .andExpect(jsonPath("$.type").value("DISCOUNT_FIXED_AMOUNT"))
        .andExpect(jsonPath("$.value").value(50000));
  }

  @Test
  void shouldRedeemVoucher() throws Exception {
    RedeemVoucherRequest request = new RedeemVoucherRequest("customer-123");
    mvc.perform(
            post("/api/vouchers/VCHR001/redeem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNoContent());
  }
}