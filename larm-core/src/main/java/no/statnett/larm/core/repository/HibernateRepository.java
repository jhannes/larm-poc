package no.statnett.larm.core.repository;

import java.io.Serializable;
import java.util.List;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateRepository implements Repository {
    private SessionFactory sessionFactory;

    public HibernateRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(Specification<T> specification) {
        Session session = sessionFactory.openSession();
        try {
            HibernateSpecification<T> hibernateSpecification = (HibernateSpecification<T>) specification;
            return hibernateSpecification.createCriteria().getExecutableCriteria(session).list();
        } finally {
            session.close();
        }
    }

    public Serializable insert(Object entity) {
        Session session = sessionFactory.openSession();
        try {
            return session.save(entity);
        } finally {
            session.close();
        }
    }



    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> entityClass) {
        Session session = sessionFactory.openSession();
        try {
            return session.createCriteria(entityClass).list();
        } finally {
            session.close();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T retrieve(Class<T> entityClass, Serializable key) {
        Session session = sessionFactory.openSession();
        try {
            return (T) session.get(entityClass, key);
        } finally {
            session.close();
        }
    }

    public void insertAll(final Object... entities) {
        execute(new RepositoryCallback() {
            @Override
            public void doInSession(Repository repository) {
                repository.insertAll(entities);
            }
        });
    }

    @Override
    public void deleteAll(final Class<?> entityType) {
        execute(new RepositoryCallback() {
            @Override
            public void doInSession(Repository repository) {
                repository.deleteAll(entityType);
            }
        });
    }

    @Override
    public void execute(RepositoryCallback repositoryCallback) {
        Session session = sessionFactory.openSession();
        HibernateSessionRepository repository = new HibernateSessionRepository(session);
        try {
            repositoryCallback.doInSession(repository);
        } finally {
            session.close();
        }
    }
}
