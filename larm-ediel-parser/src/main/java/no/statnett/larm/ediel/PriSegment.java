package no.statnett.larm.ediel;

import java.io.IOException;

import no.statnett.larm.edifact.EdifactSegmentGroup;
import no.statnett.larm.edifact.Segment;
import no.statnett.larm.edifact.SegmentSource;

import org.joda.time.Interval;

/** Price details */
@Segment("PRI")
public class PriSegment extends EdifactSegmentGroup {

    private DtmSegment processingTime;
    private RngSegment range;

    public String getPrice() {
        return getElementComponent(0, 1);
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

    public PriSegment setProcessingTime(Interval interval) {
        processingTime= new DtmSegment(interval);
        processingTime.setQualifier("324");
        return this;
    }

    public PriSegment setVolume(Long value) {
        range = new RngSegment();
        range.setQuantity(value);
        range.setUnit("Z01");
        range.setTypeQualifier("4");
        return this;
    }

    public Long getVolume() {
        return range.getQuantity();
    }

}
