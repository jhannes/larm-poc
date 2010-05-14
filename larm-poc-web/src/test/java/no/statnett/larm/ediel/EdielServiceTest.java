package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;

import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.inmemory.InmemoryRepository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.reservekraft.ReservekraftBud;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

public class EdielServiceTest {

    private Repository repository = new InmemoryRepository();
    private EdielService service = new EdielService(repository);
    private Elspotområde elspotområde = new Elspotområde("NO4");
    private Stasjonsgruppe stasjonsgruppe = new Stasjonsgruppe("NOKG00116", "Sørfjord", elspotområde);

    @Before
    public void setupServlet() {
        repository.insert(elspotområde);
        repository.insert(stasjonsgruppe);
    }

    @Test
    public void shouldLeseVolumPerioder() throws Exception {
        DateMidnight driftsdøgn = new DateMidnight(2010, 5, 10);
        DateTime bud1StartTid = driftsdøgn.toDateTime().withHourOfDay(8);
        DateTime bud1SluttTid = driftsdøgn.toDateTime().withHourOfDay(9);
        String budreferanse = "Min referanse";
        Period varighet = Period.minutes(180);
        Period hviletid = Period.minutes(120);
        Duration enTime = Duration.standardHours(1);

        LinSegment linSegment = new LinSegment();
        linSegment.setDuration(DtmSegment.withMinutes(varighet));
        linSegment.setRestingTime(DtmSegment.withMinutes(hviletid));

        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid, enTime))
                .setVolume(800L));
        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid.plusHours(1), enTime))
                .setVolume(800L));
        linSegment.addPriceSegment(new PriSegment()
                .setProcessingTime(new Interval(bud1StartTid.plusHours(2), enTime))
                .setVolume(800L));

        RffSegment rffSegment = new RffSegment();
        rffSegment.setReference(budreferanse);
        linSegment.setPriceQuote(rffSegment);

        LocSegment locSegment = new LocSegment();
        locSegment.setLocationIdentification(stasjonsgruppe.getNavn());
        linSegment.setLocation(locSegment);

        ReservekraftBud bud = service.lesBud(linSegment, driftsdøgn.toDateTime(), driftsdøgn.plusDays(1).toDateTime());

        assertThat(bud.getVolumPerioder()).hasSize(3);
        assertThat(bud.getVolumPerioder().get(0).getStartTid()).isEqualTo(bud1StartTid);
        assertThat(bud.getVolumPerioder().get(0).getSluttTid()).isEqualTo(bud1SluttTid);
        assertThat(bud.getVolumPerioder().get(0).getTilgjengeligVolum()).isEqualTo(800);

        assertThat(bud.getStasjonsgruppe()).isEqualTo(stasjonsgruppe);
        assertThat(bud.getBudreferanse()).isEqualTo(budreferanse);
        assertThat(bud.getAktiveringstid()).isEqualTo(varighet);
        assertThat(bud.getHviletid()).isEqualTo(hviletid);
        assertThat(bud.getBudperiode()).isEqualTo(new Interval(driftsdøgn, Duration.standardDays(1)));
    }

    @Test
    public void shouldParseAllTestQuotesFiles() throws Exception {
        repository.insert(new Stasjonsgruppe("NOKG00049", elspotområde));
        repository.insert(new Stasjonsgruppe("NOKG00056", elspotområde));

        File quotesFileTestDir = new File("src/test/ediel/quotes");
        assertThat(quotesFileTestDir.listFiles()).isNotEmpty();
        for (File quotesFile : quotesFileTestDir.listFiles()) {
            repository.deleteAll(ReservekraftBud.class);
            service.readMessage(new FileReader(quotesFile));
            assertThat(repository.findAll(ReservekraftBud.class)).isNotEmpty();
        }
    }

    @Test
    public void shouldCreateAperakWithReference() throws Exception {
        DateTime now = new DateTime(2010, 1, 12, 10, 11, 2, 1);
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        String ourPartyId   = "7080000923168";
        String theirPartyId = "7080005050999";

        String reference = "statkraft-987";
        AperakMessage aperak = service.createResponse(reference);
        aperak.write(System.out);

        assertThat(aperak.getBeginMessage().getMessageFunction()).isEqualTo("29"); // Accepted without amendment
        assertThat(aperak.getMessageDate().getDateTime()).isEqualTo(now.withSecondOfMinute(0).withMillisOfSecond(0));
        assertThat(aperak.getArrivalTime().getDateTime()).isEqualTo(now.withSecondOfMinute(0).withMillisOfSecond(0));
        assertThat(aperak.getReferencedMessage().getReference()).isEqualTo(reference);

        assertThat(aperak.getMessageFrom().getPartyId()).isEqualTo(ourPartyId);
        assertThat(aperak.getMessageFrom().getCity()).isEqualTo("OSLO");
        assertThat(aperak.getMessageFrom().getCountry()).isEqualTo("NO");
        assertThat(aperak.getMessageFrom().getContactInfo().getDepartment()).isEqualTo("Landsentralen");

        assertThat(aperak.getMessageFrom().getPartyId()).isEqualTo(theirPartyId);
    }
}
