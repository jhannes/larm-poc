package no.statnett.larm.ediel.servlet;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;
import no.statnett.larm.ediel.AperakParser;
import no.statnett.larm.ediel.servlet.EdielServlet;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateMidnight;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

public class EdielServletTest {

    private EdielServlet servlet = new EdielServlet();
    private Repository repository = new InmemoryRepository();
    private Elspotområde elspotområde = new Elspotområde("NO4");
    private Stasjonsgruppe stasjonsgruppe = new Stasjonsgruppe("NOKG00116", "Sørfjord", elspotområde);

    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);

    private StringWriter response = new StringWriter();
    private File quotesFile = new File("src/test/ediel/quotes/QuoteNordkraft.edi");

    @Before
    public void setupServlet() throws IOException {
        servlet.setRepository(repository);
        repository.insert(stasjonsgruppe);

        when(req.getMethod()).thenReturn("POST");
        when(req.getReader()).thenReturn(new BufferedReader(new FileReader(quotesFile)));
        when(resp.getWriter()).thenReturn(new PrintWriter(response));
    }

    @Test
    public void shouldParseQuotesMessageToRkBud() throws Exception {
        DateMidnight driftsdøgn = new DateMidnight(2009, 12, 1);
        Duration ettDøgn = Duration.standardDays(1);

        servlet.service(req, resp);

        ReservekraftBud bud = repository.findAll(ReservekraftBud.class).get(0);
        assertThat(bud.getStasjonsgruppe()).isEqualTo(stasjonsgruppe);
        assertThat(bud.getBudperiode()).isEqualTo(new Interval(driftsdøgn, ettDøgn));
    }


    @Test
    public void skalGenerereAperak() throws Exception {
        servlet.doPost(req, resp);

        AperakParser aperakParser = new AperakParser(new StringReader(response.toString()));
        assertThat(aperakParser.parseMessage()).isNotNull();
    }
}
