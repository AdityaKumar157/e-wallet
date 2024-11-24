package com.makeprojects.ewallet.shared.core.definition;

import java.util.List;
import java.util.UUID;

public interface CRUDService<T> extends Service{

    /**
     * Retrieves the T type entity from database for the specified id.
     * @param id UUID of entity
     * @return entity of type T
     */
    T get(UUID id);

    /**
     * Retrieves list of all entities from database of type T.
     * @return list of type T
     */
    List<T> getAll();

    /**
     * Creates/Adds a new entity of type T in database
     * @param entity Entity of type T which needs to be added in database
     * @return T entity which is created in database
     */
    T create(T entity);

    /**
     * Updates the existing entity of type T in database
     * @param entity Entity of type T which needs to be updated/saved in database
     * @return T entity which is updated in database
     */
    T update(T entity);

    /**
     * Deletes the existing entity with specified UUID
     * @param id UUID of entity T
     */
    void delete(UUID id);
}
