package no.statnett.larm.poc.client.stasjon;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mortbay.jetty.plus.naming.EnvEntry;

@RunWith(Parameterized.class)
public class StasjonSpecificationTest {

    private Repository repository;

    public StasjonSpecificationTest(Repository repository) {
        this.repository = repository;
    }

    @Parameterized.Parameters public static List<Object[]> repositories() throws NamingException {
        new EnvEntry("jdbc/inmemory", JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle;MVCC=true", "", ""));

        List<Object[]> result = new ArrayList<Object[]>();
        result.add(new Object[] { new LarmHibernateRepository("jdbc/inmemory") });
        result.add(new Object[] { new InmemoryRepository() });
        return result;
    }

    @Test
    public void shouldFilterByFastområde() throws Exception {
        StasjonSpecification specification = new StasjonSpecification();

        Stasjon stasjonIOmråde1 = Stasjon.medNavnOgFastområde("Stasjon 1.1", "F01");
        Stasjon stasjonIOmråde3 = Stasjon.medNavnOgFastområde("Stasjon 3", "F03");
        Stasjon stasjonIOmråde2 = Stasjon.medNavnOgFastområde("Stasjon 2.1", "F02");

        repository.insertAll(stasjonIOmråde1, Stasjon.medNavnOgFastområde("Stasjon 1.2", "F01"),
                stasjonIOmråde2, Stasjon.medNavnOgFastområde("Stasjon 2.2", "F02"), stasjonIOmråde3);

        assertThat(repository.find(specification))
                .contains(stasjonIOmråde1, stasjonIOmråde2, stasjonIOmråde3);

        specification.setIncludeF01(true);
        assertThat(repository.find(specification))
                .contains(stasjonIOmråde1)
                .excludes(stasjonIOmråde2, stasjonIOmråde3);

        specification.setIncludeF02(true);
        assertThat(repository.find(specification))
                .contains(stasjonIOmråde1, stasjonIOmråde2)
                .excludes(stasjonIOmråde3);
    }
}
