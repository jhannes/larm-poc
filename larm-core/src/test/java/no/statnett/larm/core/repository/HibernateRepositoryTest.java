package no.statnett.larm.core.repository;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

public class HibernateRepositoryTest extends AbstractRepositoryTest {
    private static Repository repository = createRepository();

    @Override
    Repository getRepository() {
        return repository;
    }

    private static Repository createRepository() {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg
                .setProperty(Environment.URL, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle")
                .setProperty(Environment.DRIVER, "org.h2.Driver")
                .setProperty(Environment.HBM2DDL_AUTO, "create");
        cfg.addAnnotatedClass(ExampleEntity.class);
        return new HibernateRepository(cfg.buildSessionFactory());
    }
}
