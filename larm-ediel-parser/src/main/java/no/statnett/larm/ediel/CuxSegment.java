package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("CUX")
public class CuxSegment extends EdifactSegment {

    public String getCurrencyCode() {
        return getAsString(0, 1);
    }

}
