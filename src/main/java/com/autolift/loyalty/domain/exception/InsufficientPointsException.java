package com.autolift.loyalty.domain.exception;

public class InsufficientPointsException extends RuntimeException {

  public InsufficientPointsException(String message) {
    super(message);
  }
}
