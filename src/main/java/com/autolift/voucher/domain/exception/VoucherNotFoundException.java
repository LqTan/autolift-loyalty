package com.autolift.voucher.domain.exception;

public class VoucherNotFoundException extends RuntimeException {

  public VoucherNotFoundException(String message) {
    super(message);
  }

  public static VoucherNotFoundException withId(String id) {
    return new VoucherNotFoundException("Voucher not found with id: " + id);
  }

  public static VoucherNotFoundException withCode(String code) {
    return new VoucherNotFoundException("Voucher not found with code: " + code);
  }
}