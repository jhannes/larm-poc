package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;

@Segment("DTM")
public class DtmSegment extends QualifiedEdifactSegment {

    public static DtmSegment withMinutes(Period period) {
        DtmSegment dtmSegment = new DtmSegment();
        dtmSegment.setValueAndFormat(String.valueOf(period.getMinutes()), "806");
        return dtmSegment;
    }

    public DtmSegment() {
    }

    public DtmSegment(Interval interval) {
        String value = interval.getStart().toString("yyyyMMddHHmm")
                + interval.getEnd().toString("yyyyMMddHHmm");
        setValueAndFormat(value, "Z13");
    }

    public String getTime() {
        return getValueAsString();
    }

    public String getHour() {
        return getValueAsString();
    }

    @Override
    public String getQualifier() {
        return getElementComponent(0, 0);
    }

    public Interval getPeriod() {
        String data = getValueAsString();
        verifyFormatQualifier("Z13");
        DateTime dateStart = DateTimeFormat.forPattern("yyyyMMddHHmm").parseDateTime(data.substring(0, 12));
        DateTime dateEnd = DateTimeFormat.forPattern("yyyyMMddHHmm").parseDateTime(data.substring(12));
        return new Interval(dateStart, dateEnd);
    }

    private void verifyFormatQualifier(String expected) {
        if (!expected.equals(getFormatQualifier())) {
            throw new IllegalArgumentException("expected:" + expected + "; got " + getFormatQualifier());
        }
    }

    private Object getFormatQualifier() {
        return getElementComponent(0, 2);
    }

    public void setQualifier(String qualifier) {
        setElementComponent(0, 0, qualifier);
    }

    private void setValueAndFormat(String value, String formatQualifier) {
        setElementComponent(0, 1, value);
        setElementComponent(0, 2, formatQualifier);
    }

    public Period getQuantity() {
        if ("806".equals(getFormatQualifier())) {
            return Period.minutes(getValueAsInt());
        } else if ("805".equals(getFormatQualifier())) {
            return Period.hours(getValueAsInt());
        }
        throw new IllegalArgumentException("No support for format qualifier " + getFormatQualifier());
    }

    private int getValueAsInt() {
        return Integer.parseInt(getValueAsString());
    }

    private String getValueAsString() {
        return getElementComponent(0, 1);
    }

    public DateTime getDateTime() {
        if ("203".equals(getFormatQualifier())) {
            return DateTimeFormat.forPattern("yyyyMMddHHmm").parseDateTime(getValueAsString());
        }
        throw new IllegalArgumentException("No support for format qualifier " + getFormatQualifier());

    }

}
