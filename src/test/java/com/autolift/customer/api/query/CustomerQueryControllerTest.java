package com.autolift.customer.api.query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.autolift.auth.ApplicationUserDetailsService;
import com.autolift.auth.JwtTokenProvider;
import com.autolift.config.SecurityConfig;
import com.autolift.customer.application.query.GetAllCustomersQuery;
import com.autolift.customer.application.query.GetAllCustomersQueryHandler;
import com.autolift.customer.application.query.GetCustomerQuery;
import com.autolift.customer.application.query.GetCustomerQueryHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GetAllCustomersQueryHandler.class, GetCustomerQueryHandler.class, SecurityConfig.class})
class CustomerQueryControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private GetAllCustomersQueryHandler getAllHandler;
  @MockBean private GetCustomerQueryHandler getHandler;
  @MockBean private JwtTokenProvider jwtTokenProvider;
  @MockBean private ApplicationUserDetailsService userDetailsService;
  @MockBean private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {}

  @Test
  void shouldGetAllCustomers() throws Exception {
    List<CustomerResponse> customerList =
        List.of(
            new CustomerResponse(
                "550e8400-e29b-41d4-a716-446655440000",
                "John Doe",
                "john@example.com",
                "0987654321",
                "NORMAL",
                "ACTIVE"),
            new CustomerResponse(
                "550e8400-e29b-41d4-a716-446655440001",
                "Jane Doe",
                "jane@example.com",
                "0987654322",
                "VIP",
                "ACTIVE"));

    Page<CustomerResponse> customerPage = new PageImpl(customerList, PageRequest.of(0, 20), 2);
    Pageable pageable = PageRequest.of(0, 20);
    when(getAllHandler.handle(any(GetAllCustomersQuery.class), any(Pageable.class)))
        .thenReturn(customerPage);

    mvc.perform(get("/api/customers").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("John Doe"))
        .andExpect(jsonPath("$.content[1].name").value("Jane Doe"))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  void shouldGetCustomerById() throws Exception {
    CustomerResponse customer =
        new CustomerResponse(
            "550e8400-e29b-41d4-a716-446655440000",
            "John Doe",
            "john@example.com",
            "0987654321",
            "NORMAL",
            "ACTIVE");

    when(getHandler.handle(new GetCustomerQuery("550e8400-e29b-41d4-a716-446655440000")))
        .thenReturn(customer);

    mvc.perform(get("/api/customers/550e8400-e29b-41d4-a716-446655440000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("John Doe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.segment").value("NORMAL"));
  }
}
