package no.statnett.larm.reservekraft;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import no.statnett.larm.nettmodell.Elspotomr�de;
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
    private Elspotomr�de no1 = new Elspotomr�de("NO1"),
        no2 = new Elspotomr�de("NO2"),
        no3 = new Elspotomr�de("NO3");

    @ReferenceData
    private Stasjonsgruppe stasjonsgruppe1 = new Stasjonsgruppe("NOKG00116", no1),
        stasjonsgruppe2 = new Stasjonsgruppe("NOKG00115", no2);

    @Test
    public void skalBegrenseTilElspotomr�der() throws Exception {
        ReservekraftBudSpecification specification = ReservekraftBudSpecification.forOmr�der(Arrays.asList(no1, no3));

        ReservekraftBud budIValgtOmr�de = new ReservekraftBud(stasjonsgruppe1);
        ReservekraftBud budIAnnetOmr�de = new ReservekraftBud(stasjonsgruppe2);

        repository.insertAll(budIValgtOmr�de, budIAnnetOmr�de);
        assertThat(repository.find(specification))
            .contains(budIValgtOmr�de).excludes(budIAnnetOmr�de);
    }

    @Test
    public void skalBegrenseTilDato() throws Exception {
        DateMidnight driftsd�gn = new DateMidnight(2010, 3, 2);
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification().setDriftsd�gn(driftsd�gn);

        ReservekraftBud budInnenDriftsd�gnet = new ReservekraftBud(stasjonsgruppe1);
        budInnenDriftsd�gnet.setStartTid(new DateTime(driftsd�gn).withHourOfDay(8));
        budInnenDriftsd�gnet.setSluttTid(new DateTime(driftsd�gn).withHourOfDay(23));

        ReservekraftBud budSomSlutterF�r = new ReservekraftBud(stasjonsgruppe1);
        budSomSlutterF�r.setStartTid(new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(8));
        budSomSlutterF�r.setSluttTid(new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStarterEtter = new ReservekraftBud(stasjonsgruppe1);
        budSomStarterEtter.setStartTid(new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(8));
        budSomStarterEtter.setSluttTid(new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStrekkesInnIDriftsd�gn = new ReservekraftBud(stasjonsgruppe1);
        budSomStrekkesInnIDriftsd�gn.setStartTid(new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(23));
        budSomStrekkesInnIDriftsd�gn.setSluttTid(new DateTime(driftsd�gn).withHourOfDay(4));

        ReservekraftBud budSomStrekkesUtAvIDriftsd�gn = new ReservekraftBud(stasjonsgruppe1);
        budSomStrekkesUtAvIDriftsd�gn.setStartTid(new DateTime(driftsd�gn).withHourOfDay(23));
        budSomStrekkesUtAvIDriftsd�gn.setSluttTid(new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(4));

        repository.insertAll(budInnenDriftsd�gnet, budSomSlutterF�r, budSomStarterEtter,
                budSomStrekkesInnIDriftsd�gn, budSomStrekkesUtAvIDriftsd�gn);
        assertThat(repository.find(specification))
            .contains(budInnenDriftsd�gnet, budSomStrekkesInnIDriftsd�gn, budSomStrekkesUtAvIDriftsd�gn)
            .excludes(budSomSlutterF�r, budSomStarterEtter);
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
