package no.statnett.larm.ediel;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;

@Segment("FTX")
public class FtxSegment extends EdifactSegment {

    public String getText() {
        return getElementData(3);
    }

}
