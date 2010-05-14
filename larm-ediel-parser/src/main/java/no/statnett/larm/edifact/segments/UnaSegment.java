package no.statnett.larm.edifact.segments;

import java.io.IOException;

import no.statnett.larm.edifact.EdifactSegment;
import no.statnett.larm.edifact.EdifactSegmentWriter;

public class UnaSegment extends EdifactSegment {

    private String serviceAdviceString;

    public UnaSegment() {
        setSegmentName("UNA");
    }

    public UnaSegment(String serviceAdviceString) {
        setSegmentName("UNA");
        this.serviceAdviceString = serviceAdviceString;
    }

    public void write(Appendable writer) throws IOException {
        writer.append(getSegmentName() + serviceAdviceString + "\n");
    }

    @Override
    public void writeTo(EdifactSegmentWriter writer) throws IOException {
        throw new UnsupportedOperationException(this + " must be written without segment writer");
    }
}
