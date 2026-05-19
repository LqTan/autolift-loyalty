package com.autolift.customer.api.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.auth.ApplicationUserDetailsService;
import com.autolift.auth.JwtTokenProvider;
import com.autolift.config.SecurityConfig;
import com.autolift.customer.application.command.CreateCustomerCommand;
import com.autolift.customer.application.command.CreateCustomerCommandHandler;
import com.autolift.customer.application.command.CreateCustomerResult;
import com.autolift.customer.application.command.SuspendCustomerCommandHandler;
import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  CreateCustomerCommandHandler.class,
  SuspendCustomerCommandHandler.class,
  SecurityConfig.class
})
class CustomerCommandControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CreateCustomerCommandHandler createHandler;
  @MockBean private SuspendCustomerCommandHandler suspendHandler;
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
  void shouldCreateCustomer() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest("John Doe", "john@example.com", "0987654321", null);

    CreateCustomerResult result =
        new CreateCustomerResult(
            "550e8400-e29b-41d4-a716-446655440000",
            "John Doe",
            "john@example.com",
            "0987654321",
            "NORMAL",
            "ACTIVE");

    when(createHandler.handle(any(CreateCustomerCommand.class))).thenReturn(result);

    mvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.segment").value("NORMAL"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  void shouldCreateCustomerWithVipSegment() throws Exception {
    CreateCustomerRequest request =
        new CreateCustomerRequest(
            "VIP Customer", "vip@example.com", "0987654321", CustomerSegment.VIP);

    CreateCustomerResult result =
        new CreateCustomerResult(
            "550e8400-e29b-41d4-a716-446655440001",
            "VIP Customer",
            "vip@example.com",
            "0987654321",
            "VIP",
            "ACTIVE");

    when(createHandler.handle(any(CreateCustomerCommand.class))).thenReturn(result);

    mvc.perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.segment").value("VIP"));
  }

  @Test
  void shouldSuspendCustomer() throws Exception {
    mvc.perform(post("/api/customers/550e8400-e29b-41d4-a716-446655440000/suspend"))
        .andExpect(status().isNoContent());
  }
}
