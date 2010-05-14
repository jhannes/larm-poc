package no.statnett.larm.edifact.segments;

import no.statnett.larm.edifact.EdifactSegment;

public class UntSegment extends EdifactSegment {

    public UntSegment() {
        setSegmentName("UNT");
    }

    public UntSegment(String segmentCount, String messageNumber) {
        this();
        setElementComponent(0, 0, segmentCount);
        setElementComponent(1, 0, messageNumber);
    }

}
