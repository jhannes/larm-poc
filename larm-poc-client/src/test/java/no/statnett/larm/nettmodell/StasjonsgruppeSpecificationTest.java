package no.statnett.larm.nettmodell;

import static org.fest.assertions.Assertions.assertThat;
import no.statnett.larm.reservekraft.ReferenceData;
import no.statnett.larm.reservekraft.RepositoryFixture;

import org.junit.Rule;
import org.junit.Test;

public class StasjonsgruppeSpecificationTest {
	
	@Rule
	public RepositoryFixture repository = new RepositoryFixture().withInmemDb().withInmemRepo();
	
	@ReferenceData
	private Elspotområde no1 = new Elspotområde("NO1");
	
	@Test
	public void shouldFindByStasjonsgruppenavn() throws Exception {
		String stasjonsGruppeNavn = "NO00115";
		Stasjonsgruppe matchendeGruppe = new Stasjonsgruppe(stasjonsGruppeNavn, no1);
		Stasjonsgruppe ikkeMatchendeGruppe = new Stasjonsgruppe("NO00114", no1);
		repository.insert(matchendeGruppe);
		repository.insert(ikkeMatchendeGruppe);
		
		assertThat(repository.find(StasjonsgruppeSpecification.medLocationId(stasjonsGruppeNavn)))
			.contains(matchendeGruppe)
			.excludes(ikkeMatchendeGruppe);
	}

}
