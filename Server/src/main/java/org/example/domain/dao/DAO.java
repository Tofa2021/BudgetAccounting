package org.example.domain.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T, ID> {
    List<T> getAll();

    Optional<T> findById(ID id);

    void update(T model);

    T save(T model);

    void delete(T model);

    void deleteById(ID id);
}
