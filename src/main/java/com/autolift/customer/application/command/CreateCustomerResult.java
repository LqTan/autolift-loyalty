package com.autolift.customer.application.command;

public record CreateCustomerResult(
    String id,
    String name,
    String email,
    String phone,
    String segment,
    String status) {}