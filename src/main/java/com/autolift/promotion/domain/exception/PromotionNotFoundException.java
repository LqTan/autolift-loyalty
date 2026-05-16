package com.autolift.promotion.domain.exception;

public class PromotionNotFoundException extends RuntimeException {

  public PromotionNotFoundException(String id) {
    super("Promotion not found with id: " + id);
  }
}
