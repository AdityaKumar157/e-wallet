package com.makeprojects.ewallet.shared.core.definition;

import java.util.List;
import java.util.UUID;

public interface CRUDService<T> extends Service{

    T get(UUID id);

    List<T> getAll();

    T create(T entity);

    T update(T entity);

    void delete(UUID id);
}
