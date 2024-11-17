package com.makeprojects.ewallet.shared.functions.suppliers;

import com.makeprojects.ewallet.shared.exceptions.NotFoundException;

import java.util.function.Supplier;

public class NotFoundSupplier implements Supplier<NotFoundException> {
    @Override
    public NotFoundException get() {
        return null;
    }
}
