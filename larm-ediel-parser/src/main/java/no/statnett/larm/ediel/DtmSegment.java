package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("DTM")
public class DtmSegment extends QualifiedEdifactSegment {

    public String getTime() {
        return getAsString(0, 1);
    }

    public String getHour() {
        return getAsString(0, 1);
    }

    @Override
    public String getQualifier() {
        return getAsString(0, 0);
    }

    public String getMinutes() {
        return getAsString(0, 1);
    }

    public String getRange() {
        return getAsString(0, 1);
    }

}
