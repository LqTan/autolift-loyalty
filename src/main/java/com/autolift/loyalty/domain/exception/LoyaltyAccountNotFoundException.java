package com.autolift.loyalty.domain.exception;

public class LoyaltyAccountNotFoundException extends RuntimeException {

  public LoyaltyAccountNotFoundException(String message) {
    super(message);
  }
}