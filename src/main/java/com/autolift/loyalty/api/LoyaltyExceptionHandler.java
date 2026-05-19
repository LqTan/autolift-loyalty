package com.autolift.loyalty.api;

import com.autolift.loyalty.domain.exception.InsufficientPointsException;
import com.autolift.loyalty.domain.exception.LoyaltyAccountNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class LoyaltyExceptionHandler {

  @ExceptionHandler(LoyaltyAccountNotFoundException.class)
  public ResponseEntity<Void> handleNotFound() {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(InsufficientPointsException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientPoints(InsufficientPointsException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
  }

  public record ErrorResponse(String error) {}
}
