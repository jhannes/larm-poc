package no.statnett.larm.poc.client.stasjon;

import no.statnett.larm.core.repository.HibernateRepository;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Parameterized.class)
public class StasjonSpecificationTest {

    private Repository repository;

    public StasjonSpecificationTest(Repository repository) {
        this.repository = repository;
    }

    @Parameterized.Parameters public static List<Object[]> repositories() {
        List<Object[]> result = new ArrayList<Object[]>();
        result.add(new Object[] { HibernateRepository.inmemoryDatabase(Stasjon.class) });
        result.add(new Object[] { new InmemoryRepository() });
        return result;
    }

    @Test
    public void shouldFilterByFastomr�de() throws Exception {
        StasjonSpecification specification = new StasjonSpecification();

        Stasjon stasjonIOmr�de1 = Stasjon.medNavnOgFastomr�de("Stasjon 1.1", "F01");
        Stasjon stasjonIOmr�de3 = Stasjon.medNavnOgFastomr�de("Stasjon 3", "F03");
        Stasjon stasjonIOmr�de2 = Stasjon.medNavnOgFastomr�de("Stasjon 2.1", "F02");

        repository.insertAll(stasjonIOmr�de1, Stasjon.medNavnOgFastomr�de("Stasjon 1.2", "F01"),
                stasjonIOmr�de2, Stasjon.medNavnOgFastomr�de("Stasjon 2.2", "F02"), stasjonIOmr�de3);

        assertThat(repository.find(specification))
                .contains(stasjonIOmr�de1, stasjonIOmr�de2, stasjonIOmr�de3);

        specification.setIncludeF01(true);
        assertThat(repository.find(specification))
                .contains(stasjonIOmr�de1)
                .excludes(stasjonIOmr�de2, stasjonIOmr�de3);

        specification.setIncludeF02(true);
        assertThat(repository.find(specification))
                .contains(stasjonIOmr�de1, stasjonIOmr�de2)
                .excludes(stasjonIOmr�de3);
    }
}
