package com.minakov.services;

import java.util.List;

public interface Service<T> {

    T get(long id);

    List<T> getAll();

    void save(T entity);

    void delete(long id);

}
