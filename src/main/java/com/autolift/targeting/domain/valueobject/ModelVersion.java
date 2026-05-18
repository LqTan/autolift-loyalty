package com.autolift.targeting.domain.valueobject;

public record ModelVersion(String value) {

  public static ModelVersion of(String value) {
    return new ModelVersion(value);
  }

  public static ModelVersion v1() {
    return new ModelVersion("v1");
  }
}