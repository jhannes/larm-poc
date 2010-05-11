package no.statnett.larm.ediel;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;

public class DtmSegmentTest {

    @Test
    public void shouldParseDateTimes() throws Exception {
        assertThat(DtmSegment.withValueAndFormat("201005100000", "203").getDateTime())
            .isEqualTo(new DateTime(2010, 5, 10, 0, 0, 0, 0));
        assertThat(DtmSegment.withValueAndFormat("201005102400", "203").getDateTime())
            .isEqualTo(new DateTime(2010, 5, 11, 0, 0, 0, 0));
    }

    @Test
    public void shouldParsePeriods() throws Exception {
        assertThat(DtmSegment.withValueAndFormat("201005101115201005101325", "Z13").getPeriod())
            .isEqualTo(new Interval(new DateTime(2010, 5, 10, 11, 15, 0, 0), new DateTime(2010, 5, 10, 13, 25, 0, 0)));

        assertThat(DtmSegment.withValueAndFormat("201005100000201005102400", "Z13").getPeriod())
            .isEqualTo(new Interval(new DateMidnight(2010, 5, 10), new DateMidnight(2010, 5, 11)));
    }

}
