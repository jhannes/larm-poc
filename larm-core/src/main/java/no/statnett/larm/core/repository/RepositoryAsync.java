package no.statnett.larm.core.repository;

import no.statnett.larm.core.async.AsyncCallback;

import java.io.Serializable;
import java.util.List;

public interface RepositoryAsync {

    void insertAll(Object[] object, AsyncCallback<Void> callback);

    <T> void find(Specification<T> specification, AsyncCallback<List<T>> callback);

    <T> void findAll(Class<T> entityType, AsyncCallback<List<T>> callback);

    void insert(Object object, AsyncCallback<Serializable> callback);

    <T> void retrieve(Class<T> entityType, Serializable serializable, AsyncCallback<T> callback);

}
