package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("LOC")
public class LocSegment extends QualifiedEdifactSegment {

    @Override
    public String getQualifier() {
        return getElementData(0);
    }

    public String getNetArea() {
        return getElementComponent(1, 0);
    }
}
