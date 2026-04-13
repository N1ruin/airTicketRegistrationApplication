package repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T save(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    T update(T entity);

    void deleteById(long id);
}
