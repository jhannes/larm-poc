package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("DTM")
public class DtmSegment extends QualifiedEdifactSegment {

    public String getTime() {
        return getElementComponent(0, 1);
    }

    public String getHour() {
        return getElementComponent(0, 1);
    }

    @Override
    public String getQualifier() {
        return getElementComponent(0, 0);
    }

    public String getMinutes() {
        return getElementComponent(0, 1);
    }

    public String getRange() {
        return getElementComponent(0, 1);
    }

}
