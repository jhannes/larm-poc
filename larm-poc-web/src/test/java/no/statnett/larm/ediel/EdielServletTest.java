package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;
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
import org.joda.time.Period;
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
        assertThat(bud.getBudperiode()).isEqualTo(
                new Interval(new DateMidnight(2009, 12, 1).toDateTime(),
                        new DateMidnight(2009, 12, 2).toDateTime()));

    }

    @Test
    public void shouldLeseVolumPerioder() throws Exception {
        DateMidnight driftsdøgn = new DateMidnight(2010, 5, 10);
        DateTime bud1StartTid = driftsdøgn.toDateTime().withHourOfDay(8);
        DateTime bud1SluttTid = driftsdøgn.toDateTime().withHourOfDay(9);
        String budreferanse = "Min referanse";
        Period varighet = Period.minutes(180);
        Period hviletid = Period.minutes(120);

        LinSegment linSegment = new LinSegment();
        linSegment.setDuration(DtmSegment.withMinutes(varighet));
        linSegment.setRestingTime(DtmSegment.withMinutes(hviletid));

        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid, bud1SluttTid))
                .setVolume(800L));
        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid.plusHours(1), bud1SluttTid.plusHours(1)))
                .setVolume(800L));
        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid.plusHours(2), bud1SluttTid.plusHours(2)))
                .setVolume(800L));

        RffSegment rffSegment = new RffSegment();
        rffSegment.setReference(budreferanse);
        linSegment.setPriceQuote(rffSegment);

        LocSegment locSegment = new LocSegment();
        locSegment.setLocationIdentification(stasjonsgruppe.getNavn());
        linSegment.setLocation(locSegment);

        ReservekraftBud bud = servlet.lesBud(linSegment, driftsdøgn.toDateTime(), driftsdøgn.plusDays(1).toDateTime());

        assertThat(bud.getVolumPerioder()).hasSize(3);
        assertThat(bud.getVolumPerioder().get(0).getStartTid()).isEqualTo(bud1StartTid);
        assertThat(bud.getVolumPerioder().get(0).getSluttTid()).isEqualTo(bud1SluttTid);
        assertThat(bud.getVolumPerioder().get(0).getTilgjengeligVolum()).isEqualTo(800);

        assertThat(bud.getStasjonsgruppe()).isEqualTo(stasjonsgruppe);
        assertThat(bud.getBudreferanse()).isEqualTo(budreferanse);
        assertThat(bud.getAktiveringstid()).isEqualTo(varighet);
        assertThat(bud.getHviletid()).isEqualTo(hviletid);
        assertThat(bud.getBudperiode()).isEqualTo(new Interval(driftsdøgn, driftsdøgn.plusDays(1)));
    }

}
