package no.statnett.larm.edifact;

import java.io.IOException;

public interface SegmentGroup {

    void readSegmentGroup(SegmentSource segmentSource) throws IOException;

}
