package no.statnett.larm.edifact;

import java.io.IOException;


public abstract class EdifactSegmentGroup extends EdifactSegment implements SegmentGroup {

    @Override
    public void writeTo(EdifactSegmentWriter writer) throws IOException {
        throw new UnsupportedOperationException(this + " is a segment group and must override writing");
    }
}
