package no.statnett.larm.core.repository;

import java.io.Serializable;
import java.util.List;

import no.statnett.larm.core.repository.hibernate.HibernateSpecification;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

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

    public static HibernateRepository withDatabase(String databaseUrl, Class<?>... entities) {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg
                .setProperty(Environment.URL, databaseUrl)
                .setProperty(Environment.DRIVER, "org.h2.Driver")
                .setProperty(Environment.HBM2DDL_AUTO, "update");
        addAnnotatedEntities(cfg, entities);
        return new HibernateRepository(cfg.buildSessionFactory());
    }

    private static void addAnnotatedEntities(AnnotationConfiguration cfg, Class<?>[] entities) {
        for (Class<?> entity : entities) {
            cfg.addAnnotatedClass(entity);
        }
    }

    public static HibernateRepository inmemoryDatabase(Class<?>... entities) {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg
                .setProperty(Environment.URL, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle;MVCC=true")
                .setProperty(Environment.DRIVER, "org.h2.Driver")
                .setProperty(Environment.HBM2DDL_AUTO, "update");
        addAnnotatedEntities(cfg, entities);
        return new HibernateRepository(cfg.buildSessionFactory());
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
