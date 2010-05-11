package no.statnett.larm;

import no.statnett.larm.core.repository.HibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.poc.client.stasjon.Stasjon;
import no.statnett.larm.reservekraft.ReservekraftBud;
import no.statnett.larm.reservekraft.Volumperiode;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

public class LarmHibernateRepository extends HibernateRepository {

    private static final Class<?>[] ALL_ENTITIES = new Class[] {
        Stasjonsgruppe.class, Elspotområde.class,
        ReservekraftBud.class, Volumperiode.class,
        Stasjon.class
    };

    private LarmHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public static Repository withJndiUrl(String jndiName) {
        AnnotationConfiguration cfg = new AnnotationConfiguration()
            .setProperty(Environment.DATASOURCE, jndiName)
            .setProperty(Environment.HBM2DDL_AUTO, "update");
        addEntityTypes(cfg);
        return new LarmHibernateRepository(cfg.buildSessionFactory());
    }

    public static Repository withJdbcUrl(String jdbcUrl, String driver) {
        AnnotationConfiguration cfg = new AnnotationConfiguration()
            .setProperty(Environment.URL, jdbcUrl)
            .setProperty(Environment.DRIVER, driver)
            .setProperty(Environment.HBM2DDL_AUTO, "update");
        addEntityTypes(cfg);
        return new LarmHibernateRepository(cfg.buildSessionFactory());
    }

    public static Repository withInmemoryDb() {
        return withJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle;MVCC=true", "org.h2.Driver");
    }

    public static Repository withFileDb() {
        return withJdbcUrl("jdbc:h2:file:target/testdb;MODE=Oracle", "org.h2.Driver");
    }

    private static void addEntityTypes(AnnotationConfiguration cfg) {
        for (Class<?> entityType : ALL_ENTITIES) {
            cfg.addAnnotatedClass(entityType);
        }
    }

}
