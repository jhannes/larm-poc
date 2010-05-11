package no.statnett.larm.core.repository;

import java.io.Serializable;
import java.util.List;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;

import org.hibernate.Session;

/**
 * Models as a Repository the interaction
 * of a single open session. Should only be
 * used from HibernateRepository
 */
class HibernateSessionRepository implements Repository {

    private final Session session;

    public HibernateSessionRepository(Session session) {
        this.session = session;
    }

    @Override
    public void deleteAll(Class<?> entityType) {
        session.createQuery("delete " + entityType.getName());
    }

    @Override
    public void execute(RepositoryCallback repositoryCallback) {
        repositoryCallback.doInSession(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> find(Specification<T> specification) {
        HibernateSpecification<T> hibernateSpecification = (HibernateSpecification<T>) specification;
        return hibernateSpecification.createCriteria().getExecutableCriteria(session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return session.createCriteria(entityClass).list();
    }

    @Override
    public Serializable insert(Object entity) {
        return session.save(entity);
    }

    @Override
    public void insertAll(Object... entities) {
        for (Object entity : entities) {
            insert(entity);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T retrieve(Class<T> entityClass, Serializable key) {
        return (T) session.get(entityClass, key);
    }

}
