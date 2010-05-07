package no.statnett.larm.reservekraft;

import java.io.Serializable;
import java.util.List;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryCallback;
import no.statnett.larm.core.repository.Specification;

public class RepositoryFixture implements Repository {

    private boolean withInmemDb;
    private boolean withInmemRepo;

    public RepositoryFixture withInmemDb() {
        withInmemDb = true;
        return this;
    }

    public RepositoryFixture withInmemRepo() {
        withInmemRepo = true;
        return this;
    }

    @Override
    public void deleteAll(Class<?> entityType) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void execute(RepositoryCallback repositoryCallback) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <T> List<T> find(Specification<T> specification) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Serializable insert(Object entity) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void insertAll(Object... entities) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <T> T retrieve(Class<T> entityClass, Serializable key) {
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
