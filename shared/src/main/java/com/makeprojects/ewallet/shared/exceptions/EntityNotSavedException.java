package com.makeprojects.ewallet.shared.exceptions;

public class EntityNotSavedException extends RuntimeException {

    private Class<?> clazz;
    private String field;
    private Object value;

    public EntityNotSavedException(Class<?> clazz, String field, Object value) {
        super(String.format("Cannot save %s with the field: %s with value %s", clazz, field, value.toString()));
        this.clazz = clazz;
        this.field = field;
        this.value = value;
    }

    public EntityNotSavedException(String message) {
        super(message);
    }
}
