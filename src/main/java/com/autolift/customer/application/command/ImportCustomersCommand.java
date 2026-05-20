package com.autolift.customer.application.command;

import java.time.Instant;

public record ImportCustomersCommand(String filePath) {}