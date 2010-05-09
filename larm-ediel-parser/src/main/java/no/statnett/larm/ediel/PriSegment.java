package no.statnett.larm.ediel;

import java.io.IOException;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

/** Price details */
@Segment("PRI")
public class PriSegment extends EdifactSegment {

    private DtmSegment processingTime;
    private RngSegment range;

    public String getPrice() {
        return getAsString(0, 1);
    }

    public RngSegment getRange() {
        return range;
    }

    public DtmSegment getProcessingTime() {
        return processingTime;
    }

    public void readSegmentGroup(SegmentSource segmentSource) throws IOException {
        range = segmentSource.readOptionalSegment(RngSegment.class);
        processingTime = segmentSource.readOptionalSegment(DtmSegment.class, "324");
    }

}
