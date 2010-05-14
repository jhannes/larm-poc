package no.statnett.larm.edifact;

import java.io.IOException;

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
    public void write(EdifactSegmentWriter writer) throws IOException {
        throw new UnsupportedOperationException(this + " must be written without segment writer");
    }
}
