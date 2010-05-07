package no.statnett.larm.reservekraft;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Rule;
import org.junit.Test;

public class ReservekraftBudSpecificationTest {

    @Rule
    private RepositoryFixture repository = new RepositoryFixture().withInmemDb().withInmemRepo();

    @ReferenceData
    private Elspotområde no1 = new Elspotområde("NO1"),
        no2 = new Elspotområde("NO2"),
        no3 = new Elspotområde("NO3");

    @ReferenceData
    private Stasjonsgruppe stasjonsgruppe1 = new Stasjonsgruppe("NOKG00116", no1),
        stasjonsgruppe2 = new Stasjonsgruppe("NOKG00115", no2);

    @Test
    public void skalBegrenseTilElspotområder() throws Exception {
        ReservekraftBudSpecification specification = ReservekraftBudSpecification.forOmråder(Arrays.asList(no1, no3));

        ReservekraftBud budIValgtOmråde = new ReservekraftBud(stasjonsgruppe1);
        ReservekraftBud budIAnnetOmråde = new ReservekraftBud(stasjonsgruppe2);

        repository.insertAll(budIValgtOmråde, budIAnnetOmråde);
        assertThat(repository.find(specification))
            .contains(budIValgtOmråde).excludes(budIAnnetOmråde);
    }

    @Test
    public void skalBegrenseTilDato() throws Exception {
        DateMidnight driftsdøgn = new DateMidnight(2010, 3, 2);
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification().setDriftsdøgn(driftsdøgn);

        ReservekraftBud budInnenDriftsdøgnet = new ReservekraftBud(stasjonsgruppe1);
        budInnenDriftsdøgnet.setStartTid(new DateTime(driftsdøgn).withHourOfDay(8));
        budInnenDriftsdøgnet.setSluttTid(new DateTime(driftsdøgn).withHourOfDay(23));

        ReservekraftBud budSomSlutterFør = new ReservekraftBud(stasjonsgruppe1);
        budSomSlutterFør.setStartTid(new DateTime(driftsdøgn.minusDays(1)).withHourOfDay(8));
        budSomSlutterFør.setSluttTid(new DateTime(driftsdøgn.minusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStarterEtter = new ReservekraftBud(stasjonsgruppe1);
        budSomStarterEtter.setStartTid(new DateTime(driftsdøgn.plusDays(1)).withHourOfDay(8));
        budSomStarterEtter.setSluttTid(new DateTime(driftsdøgn.plusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStrekkesInnIDriftsdøgn = new ReservekraftBud(stasjonsgruppe1);
        budSomStrekkesInnIDriftsdøgn.setStartTid(new DateTime(driftsdøgn.minusDays(1)).withHourOfDay(23));
        budSomStrekkesInnIDriftsdøgn.setSluttTid(new DateTime(driftsdøgn).withHourOfDay(4));

        ReservekraftBud budSomStrekkesUtAvIDriftsdøgn = new ReservekraftBud(stasjonsgruppe1);
        budSomStrekkesUtAvIDriftsdøgn.setStartTid(new DateTime(driftsdøgn).withHourOfDay(23));
        budSomStrekkesUtAvIDriftsdøgn.setSluttTid(new DateTime(driftsdøgn.plusDays(1)).withHourOfDay(4));

        repository.insertAll(budInnenDriftsdøgnet, budSomSlutterFør, budSomStarterEtter,
                budSomStrekkesInnIDriftsdøgn, budSomStrekkesUtAvIDriftsdøgn);
        assertThat(repository.find(specification))
            .contains(budInnenDriftsdøgnet, budSomStrekkesInnIDriftsdøgn, budSomStrekkesUtAvIDriftsdøgn)
            .excludes(budSomSlutterFør, budSomStarterEtter);
    }

    @Test
    public void skalBegrenseTilBudMedVolumITidsrom() throws Exception {
        DateTime startTid = new DateTime(2010, 5, 6, 11, 0, 0, 0);
        DateTime sluttTid = startTid.plusHours(2);

        ReservekraftBud budMedVolumITidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedVolumITidsrom.setVolumForTidsrom(startTid.minusHours(2), sluttTid.minusHours(2), 300);
        budMedVolumITidsrom.setVolumForTidsrom(startTid, sluttTid, 300);

        ReservekraftBud budMedVolumIDelerAvTidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedVolumIDelerAvTidsrom.setVolumForTidsrom(startTid.minusHours(1), startTid.minusHours(1), 200);

        ReservekraftBud budMedNullVolumITidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedNullVolumITidsrom.setVolumForTidsrom(startTid, sluttTid, 0);

        ReservekraftBud budMedVolumUtenforTidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedVolumUtenforTidsrom.setVolumForTidsrom(startTid.minusHours(1), startTid, 100);
        budMedVolumUtenforTidsrom.setVolumForTidsrom(sluttTid, sluttTid.plusHours(2), 100);

        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.setDriftsperiode(new Interval(startTid, sluttTid));

        repository.insertAll(budMedVolumITidsrom, budMedVolumIDelerAvTidsrom, budMedNullVolumITidsrom, budMedVolumUtenforTidsrom);

        assertThat(repository.find(specification))
            .contains(budMedNullVolumITidsrom, budMedVolumIDelerAvTidsrom)
            .excludes(budMedNullVolumITidsrom, budMedVolumUtenforTidsrom);
    }
}
