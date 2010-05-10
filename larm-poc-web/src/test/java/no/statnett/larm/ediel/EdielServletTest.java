package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

public class EdielServletTest {
	
	private EdielServlet servlet = new EdielServlet();
	private Repository repository = new InmemoryRepository();
	private Elspotområde elspotområde = new Elspotområde("NO4");
	private Stasjonsgruppe stasjonsgruppe = new Stasjonsgruppe("NOKG00116", "Sørfjord", elspotområde);
	
	
	@Before
	public void setupServlet() {
		servlet.setRepository(repository);
		repository.insert(stasjonsgruppe);
	}

	@Test
	public void shouldParseQuotesMessageToRkBud() throws Exception {

		
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);
		
		when(req.getMethod()).thenReturn("POST");
		File quotesFile = new File("src/test/ediel/quotes/QuoteNordkraft.edi");
		
		when(req.getReader()).thenReturn(new BufferedReader(new FileReader(quotesFile)));
		
		servlet.service(req, resp);
		
		ReservekraftBud bud = repository.findAll(ReservekraftBud.class).get(0);
		
		assertThat(bud.getStasjonsgruppe()).isEqualTo(stasjonsgruppe);
		
		
	}

	@Test
	public void shouldLeseVolumPerioder() throws Exception {
		DateMidnight driftsdøgn = new DateMidnight(2010, 5, 10);
		DateTime bud1StartTid = driftsdøgn.toDateTime().withHourOfDay(8);
		DateTime bud1SluttTid = driftsdøgn.toDateTime().withHourOfDay(9);

		LinSegment linSegment = new LinSegment();
		
		PriSegment priSegment1 = new PriSegment();
	//	priSegment1.setProcessingTime(new Interval(bud1SluttTid, bud1SluttTid));
		linSegment.addPriceSegment(priSegment1);
		PriSegment priSegment2 = new PriSegment();
		linSegment.addPriceSegment(priSegment2);
		PriSegment priSegment3 = new PriSegment();
		linSegment.addPriceSegment(priSegment3);
		
		LocSegment locSegment = new LocSegment();
		locSegment.setLocationIdentification(stasjonsgruppe.getNavn());
		linSegment.setLocation(locSegment);
		
		
		
		ReservekraftBud bud = servlet.lesBud(linSegment);
		
		assertThat(bud.getVolumPerioder()).hasSize(3);
		assertThat(bud.getVolumPerioder().get(0).getStartTid()).isEqualTo(bud1StartTid);
		assertThat(bud.getVolumPerioder().get(0).getSluttTid()).isEqualTo(bud1SluttTid);
		assertThat(bud.getVolumPerioder().get(0).getTilgjengeligVolum()).isEqualTo(800);
		
		
	}
	
}
