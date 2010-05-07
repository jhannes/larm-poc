package no.statnett.larm.ediel;

import no.statnett.larm.edifact.QualifiedEdifactSegment;
import no.statnett.larm.edifact.Segment;

/**
 * Name And Address
 */
@Segment("NAD")
public class NadSegment extends QualifiedEdifactSegment {

    public String getPartyId() {
        return getElementComponent(1, 0);
    }

    @Override
    public String getQualifier() {
        return getElementData(0);
    }

}
