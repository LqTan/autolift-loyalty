package com.autolift.customer.api.query;

public record CustomerResponse(
    String id, String name, String email, String phone, String segment, String status) {}
