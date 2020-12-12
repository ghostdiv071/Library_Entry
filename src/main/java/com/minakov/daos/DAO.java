package com.minakov.daos;

import java.util.List;

public interface DAO<T> {

    T get(long id);

    List<T> getAll();

    void create(T entity);

    void update(long id, T entity);

    void delete(long id);

}
