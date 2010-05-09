package no.statnett.larm.edifact;

import java.io.IOException;

public interface SegmentSource {

    <T extends EdifactSegment> T readOptionalSegment(String segmentName)
            throws IOException;

    <T extends EdifactSegment> T readMandatorySegment(String segmentName)
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