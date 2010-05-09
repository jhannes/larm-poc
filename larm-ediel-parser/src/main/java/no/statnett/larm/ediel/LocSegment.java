package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("LOC")
public class LocSegment extends QualifiedEdifactSegment {

    @Override
    public String getQualifier() {
        return getAsString(0);
    }

    public String getNetArea() {
        return getAsString(1, 0);
    }
}
