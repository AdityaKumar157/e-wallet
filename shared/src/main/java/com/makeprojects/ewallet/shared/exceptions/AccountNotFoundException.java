package com.makeprojects.ewallet.shared.exceptions;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends NotFoundException{
    private Exception innerException;

    public AccountNotFoundException(Class<?> clazz) {
        super(clazz);
    }

    public AccountNotFoundException(Class<?> clazz, Exception innerException) {
        super(clazz);
        this.innerException = innerException;
    }
}
