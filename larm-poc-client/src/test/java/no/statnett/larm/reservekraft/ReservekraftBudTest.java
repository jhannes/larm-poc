package no.statnett.larm.reservekraft;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

public class ReservekraftBudTest {

    @Test
    public void skalHaVolumPerioder() throws Exception {
        DateTime driftsdøgn = new DateMidnight(2010, 3, 2).toDateTime();

        ReservekraftBud bud = new ReservekraftBud();
        bud.setVolumForTidsrom(driftsdøgn.withHourOfDay(1), driftsdøgn.withHourOfDay(2), 200L);
        bud.setVolumForTidsrom(driftsdøgn.withHourOfDay(3), driftsdøgn.withHourOfDay(5), 300L);
        bud.setVolumForTidsrom(driftsdøgn.withHourOfDay(6), driftsdøgn.withHourOfDay(7), 400L);

        assertThat(bud.getVolumPerioder()).hasSize(3);
        assertThat(bud.getVolumPerioder().get(1).getStartTid()).isEqualTo(driftsdøgn.withHourOfDay(3));
        assertThat(bud.getVolumPerioder().get(1).getSluttTid()).isEqualTo(driftsdøgn.withHourOfDay(5));
        assertThat(bud.getVolumPerioder().get(1).getTilgjengeligVolum()).isEqualTo(300);
    }

}
