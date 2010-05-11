package no.statnett.larm.ediel;

import java.io.IOException;

import no.statnett.larm.edifact.EdifactSegmentGroup;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

@Segment("ERC")
public class ErcSegment extends EdifactSegmentGroup {

    private FtxSegment freeText;

    public void readSegmentGroup(SegmentSource segmentSource) throws IOException {
        freeText = segmentSource.readOptionalSegment(FtxSegment.class);
    }

    public String getApplicationError() {
        return getElementComponent(0, 2);
    }

    public FtxSegment getFreeText() {
        return freeText;
    }

}
