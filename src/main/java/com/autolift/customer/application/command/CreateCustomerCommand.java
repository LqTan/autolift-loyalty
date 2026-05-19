package com.autolift.customer.application.command;

import com.autolift.customer.domain.valueobject.CustomerSegment;

public record CreateCustomerCommand(
    String name, String email, String phone, CustomerSegment segment) {}
