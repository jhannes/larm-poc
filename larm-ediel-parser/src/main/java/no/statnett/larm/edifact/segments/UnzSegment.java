package no.statnett.larm.edifact.segments;

import no.statnett.larm.edifact.EdifactSegment;

public class UnzSegment extends EdifactSegment {

    public UnzSegment() {
        setSegmentName("UNZ");
    }

    public UnzSegment(String unknown1, String unknown2) {
        this();
        setElementComponent(0, 0, unknown1);
        setElementComponent(1, 0, unknown2);
    }
}
