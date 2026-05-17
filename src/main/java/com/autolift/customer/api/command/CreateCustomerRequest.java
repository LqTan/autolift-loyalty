package com.autolift.customer.api.command;

import com.autolift.customer.domain.valueobject.CustomerSegment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    String phone,
    CustomerSegment segment) {

  public CustomerSegment segment() {
    return segment != null ? segment : CustomerSegment.NORMAL;
  }
}