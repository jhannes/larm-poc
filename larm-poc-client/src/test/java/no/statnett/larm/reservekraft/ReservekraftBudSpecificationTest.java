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
    public RepositoryFixture repository = new RepositoryFixture().withInmemDb().withInmemRepo();

    @ReferenceData
    private Elspotomr�de no1 = new Elspotomr�de("NO1");
    @ReferenceData
    private Elspotomr�de no2 = new Elspotomr�de("NO2"),
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
        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.setDriftsd�gn(driftsd�gn);

        ReservekraftBud budInnenDriftsd�gnet = budMedTidsintervall(stasjonsgruppe1,
                new DateTime(driftsd�gn).withHourOfDay(8),
                new DateTime(driftsd�gn).withHourOfDay(23));

        ReservekraftBud budSomSlutterF�r = budMedTidsintervall(stasjonsgruppe1,
                new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(8),
                new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStarterEtter = budMedTidsintervall(stasjonsgruppe1,
                new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(8),
                new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(23));

        ReservekraftBud budSomStrekkesInnIDriftsd�gn = budMedTidsintervall(stasjonsgruppe1,
                new DateTime(driftsd�gn.minusDays(1)).withHourOfDay(23),
                new DateTime(driftsd�gn).withHourOfDay(4));

        ReservekraftBud budSomStrekkesUtAvIDriftsd�gn = budMedTidsintervall(stasjonsgruppe1,
                new DateTime(driftsd�gn).withHourOfDay(23),
                new DateTime(driftsd�gn.plusDays(1)).withHourOfDay(4));

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
        budMedVolumIDelerAvTidsrom.setVolumForTidsrom(startTid.minusHours(1), sluttTid.minusHours(1), 200);

        ReservekraftBud budMedNullVolumITidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedNullVolumITidsrom.setVolumForTidsrom(startTid, sluttTid, 0);

        ReservekraftBud budMedVolumUtenforTidsrom = new ReservekraftBud(stasjonsgruppe1);
        budMedVolumUtenforTidsrom.setVolumForTidsrom(startTid.minusHours(1), startTid, 100);
        budMedVolumUtenforTidsrom.setVolumForTidsrom(sluttTid, sluttTid.plusHours(2), 100);

        ReservekraftBudSpecification specification = new ReservekraftBudSpecification();
        specification.setDriftsperiode(new Interval(startTid, sluttTid));

        repository.insertAll(budMedVolumITidsrom, budMedVolumIDelerAvTidsrom, budMedNullVolumITidsrom, budMedVolumUtenforTidsrom);

        assertThat(repository.find(specification))
            .contains(budMedVolumITidsrom)
            .excludes(budMedVolumUtenforTidsrom)
            .excludes(budMedNullVolumITidsrom)
            .contains(budMedVolumIDelerAvTidsrom);
    }

    private ReservekraftBud budMedTidsintervall(Stasjonsgruppe stasjonsgruppe, DateTime startTid, DateTime sluttTid) {
        ReservekraftBud reservekraftBud = new ReservekraftBud(stasjonsgruppe);
        reservekraftBud.setStartTid(startTid);
        reservekraftBud.setSluttTid(sluttTid);
        return reservekraftBud;
    }
}
