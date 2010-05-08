package no.statnett.larm.reservekraft;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

public class ReservekraftBudTest {

    @Test
    public void skalHaVolumPerioder() throws Exception {
        DateTime driftsd�gn = new DateMidnight(2010, 3, 2).toDateTime();

        ReservekraftBud bud = new ReservekraftBud();
        bud.setVolumForTidsrom(driftsd�gn.withHourOfDay(1), driftsd�gn.withHourOfDay(2), 200);
        bud.setVolumForTidsrom(driftsd�gn.withHourOfDay(3), driftsd�gn.withHourOfDay(5), 300);
        bud.setVolumForTidsrom(driftsd�gn.withHourOfDay(6), driftsd�gn.withHourOfDay(7), 400);

        assertThat(bud.getVolumPerioder()).hasSize(3);
        assertThat(bud.getVolumPerioder().get(1).getStartTid()).isEqualTo(driftsd�gn.withHourOfDay(3));
        assertThat(bud.getVolumPerioder().get(1).getSluttTid()).isEqualTo(driftsd�gn.withHourOfDay(5));
        assertThat(bud.getVolumPerioder().get(1).getTilgjengeligVolum()).isEqualTo(300);
    }

}
