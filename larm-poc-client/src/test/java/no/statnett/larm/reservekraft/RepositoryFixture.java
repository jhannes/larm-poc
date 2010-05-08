package no.statnett.larm.reservekraft;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import no.statnett.larm.core.repository.HibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryCallback;
import no.statnett.larm.core.repository.Specification;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RepositoryFixture implements Repository, MethodRule {

    private static final Class<?>[] ALL_ENTITIES = new Class[] {
        Stasjonsgruppe.class, Elspotområde.class,
        ReservekraftBud.class, Volumperiode.class
    };

    private List<Repository> repositories = new ArrayList<Repository>();

    private Repository repository = null;

    public void deleteAll(Class<?> entityType) {
        repository.deleteAll(entityType);
    }

    public void execute(RepositoryCallback repositoryCallback) {
        repository.execute(repositoryCallback);
    }

    public <T> List<T> find(Specification<T> specification) {
        return repository.find(specification);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return repository.findAll(entityClass);
    }

    public Serializable insert(Object entity) {
        return repository.insert(entity);
    }

    public void insertAll(Object... entities) {
        repository.insertAll(entities);
    }

    public <T> T retrieve(Class<T> entityClass, Serializable key) {
        return repository.retrieve(entityClass, key);
    }

    public RepositoryFixture withInmemDb() {
        repositories.add(new InmemoryRepository());
        return this;
    }

    public RepositoryFixture withInmemRepo() {
        repositories.add(HibernateRepository.inmemoryDatabase(ALL_ENTITIES));
        return this;
    }

    @Override
    public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                for (Repository repository : repositories) {
                    RepositoryFixture.this.repository = repository;
                    insertReferenceData(target);
                    base.evaluate();
                }
            }
        };
    }

    protected void insertReferenceData(Object target) {
        for (Field field : target.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(ReferenceData.class) == null) continue;
            repository.deleteAll(field.getType());
        }
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(ReferenceData.class) == null) continue;
            try {
                field.setAccessible(true);
                repository.insert(field.get(target));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Can not happen");
            }
        }
    }

}
