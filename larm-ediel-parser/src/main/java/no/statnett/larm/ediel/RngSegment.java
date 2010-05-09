package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

/** Range */
@Segment("RNG")
public class RngSegment extends EdifactSegment {

    public String getUnit() {
        return getAsString(1, 0);
    }

    public String getMinimum() {
        return getAsString(1, 1);
    }

}
