package no.statnett.larm.edifact;

import java.io.IOException;

public interface SegmentSource {

    EdifactSegment readOptionalSegment(String segmentName)
            throws IOException;

    EdifactSegment readMandatorySegment(String segmentName)
            throws IOException;

    <T extends EdifactSegment> T readOptionalSegment(Class<T> segmentClass)
            throws IOException;

    <T extends QualifiedEdifactSegment> T readOptionalSegment(Class<T> segmentClass, String qualifier)
            throws IOException;

    <T extends EdifactSegment> T readMandatorySegment(Class<T> segmentClass)
            throws IOException;

    <T extends QualifiedEdifactSegment> T readMandatorySegment(Class<T> segmentClass, String qualifier)
            throws IOException;

}