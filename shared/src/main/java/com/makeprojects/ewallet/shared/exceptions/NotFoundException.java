package com.makeprojects.ewallet.shared.exceptions;

public class NotFoundException extends RuntimeException {

  private Class<?> clazz;
  private String field;
  private Object value;

  public NotFoundException(Class<?> clazz, String field, Object value) {
    super(String.format("Cannot find %s with the field: %s with value %s", clazz, field, value.toString()));
    this.clazz = clazz;
    this.field = field;
    this.value = value;
  }

  public NotFoundException(Class<?> clazz) {
    super(String.format("Cannot find any %s.", clazz));
    this.clazz = clazz;
  }

}
