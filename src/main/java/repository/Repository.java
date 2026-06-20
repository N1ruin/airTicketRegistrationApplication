package repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    T create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T update(T entity);

    void deleteById(ID id);
}
