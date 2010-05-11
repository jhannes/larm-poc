package no.statnett.larm;

import no.statnett.larm.core.repository.HibernateRepository;
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

    public LarmHibernateRepository(String jndiName) {
        super(createSessionFactory(jndiName));
    }

    private static SessionFactory createSessionFactory(String jndiName) {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg.setProperty(Environment.DATASOURCE, jndiName)
            .setProperty(Environment.HBM2DDL_AUTO, "update");

        for (Class<?> entityType : ALL_ENTITIES) {
            cfg.addAnnotatedClass(entityType);
        }
        return cfg.buildSessionFactory();
    }

}
