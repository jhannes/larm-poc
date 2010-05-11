package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@Segment("DTM")
public class DtmSegment extends QualifiedEdifactSegment {

    public static DtmSegment withValueAndFormat(String value, String formatQualifier) {
        DtmSegment dtmSegment = new DtmSegment();
        dtmSegment.setValueAndFormat(value, formatQualifier);
        return dtmSegment;
    }

    public static DtmSegment withMinutes(Period period) {
        return withValueAndFormat(String.valueOf(period.getMinutes()), "806");
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
        DateTime dateStart = parseDateTime(data.substring(0, 12));
        DateTime dateEnd = parseDateTime(data.substring(12));
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
            return parseDateTime(getValueAsString());
        }
        throw new IllegalArgumentException("No support for format qualifier " + getFormatQualifier());

    }

    private DateTime parseDateTime(String valueAsString) {
        DateTimeFormatter yyyyMMdd = DateTimeFormat.forPattern("yyyyMMdd");
        DateTimeFormatter yyyyMMddHHmm = DateTimeFormat.forPattern("yyyyMMddHHmm");
        if (valueAsString.endsWith("2400")) {
            return yyyyMMdd.parseDateTime(valueAsString.substring(0,8)).plusDays(1);
        }
        return yyyyMMddHHmm.parseDateTime(valueAsString);
    }

}
