package no.statnett.larm.core.repository;

import java.io.Serializable;
import java.util.List;

public interface Repository {
    <T> List<T> find(Specification<T> specification);

    Serializable insert(Object entity);

    <T> List<T> findAll(Class<T> entityClass);

    <T> T retrieve(Class<T> entityClass, Serializable key);

    void insertAll(Object... entities);

    void deleteAll(Class<?> entityType);

    void execute(RepositoryCallback repositoryCallback);
}
