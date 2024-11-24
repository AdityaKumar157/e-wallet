package com.makeprojects.ewallet.shared.core.definition;

public interface ValidationService<T> extends Service {

    /**
     * Validates the T type object
     * @param obj Object of type T
     * @return true if T obj is valid, otherwise false
     */
    boolean validate(T obj);
}
